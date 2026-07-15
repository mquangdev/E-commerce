package e_commerce.catalog_service.commands.services.impl;

import e_commerce.catalog_service.commands.dtos.request.CategoryCreateRequest;
import e_commerce.catalog_service.commands.dtos.request.CategoryImportDto;
import e_commerce.catalog_service.commands.dtos.request.OutboxEventRequest;
import e_commerce.catalog_service.commands.dtos.response.CategoryResponse;
import e_commerce.catalog_service.commands.dtos.request.CategoryUpdateRequest;
import e_commerce.catalog_service.commands.dtos.response.ImportResultResponse;
import e_commerce.catalog_service.commands.dtos.response.RowErrorDetail;
import e_commerce.catalog_service.commands.entities.CategoryEntity;
import e_commerce.catalog_service.commands.repositories.CategoryRepository;
import e_commerce.catalog_service.commands.services.CategoryCommandService;
import e_commerce.catalog_service.commands.services.OutboxEventService;
import e_commerce.common_shared.exception.ResourceAlreadyExistsException;
import e_commerce.common_shared.exception.ResourceNotFoundException;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final OutboxEventService outboxEventService;
    private final int BATCH_SIZE = 500;
    private final Validator validator;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại");
        }

        CategoryEntity category = CategoryEntity.builder().id(UUID.randomUUID()).name(request.getName()).description(request.getDescription()).isActive(request.getIsActive() != null ? request.getIsActive() : true).build();

        CategoryEntity saved = categoryRepository.save(category);
        outboxEventService.insertOutboxEvent(OutboxEventRequest.builder().eventType("CREATE").aggregateType("categories").aggregateId(saved.getId()).payloadObj(saved).build());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {
        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            category.setActive(request.getIsActive());
        }

        CategoryEntity saved = categoryRepository.save(category);
        outboxEventService.insertOutboxEvent(OutboxEventRequest.builder().eventType("UPDATE").aggregateType("categories").aggregateId(saved.getId()).payloadObj(saved).build());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        category.setDeleted(true);
        categoryRepository.save(category);
        outboxEventService.insertOutboxEvent(OutboxEventRequest.builder().eventType("DELETE").aggregateType("categories").aggregateId(id).build());
    }

    @Override
    public ImportResultResponse importExcelFile(MultipartFile file) {
        try (InputStream is = file.getInputStream(); ReadableWorkbook wb = new ReadableWorkbook(is)) {
            // Lấy trang đầu tiên
            Sheet firstSheet = wb.getFirstSheet();

            List<CategoryEntity> batchBuffer = new ArrayList<>();

            ImportResultResponse result = ImportResultResponse.builder().errorCount(0).successCount(0).totalRows(0).errors(new ArrayList<>()).build();

            // Mở luồng đọc từng dòng chống tràn RAM
            firstSheet.openStream().skip(1) // Bỏ qua dòng đầu tiên là tiêu đề (STT, Tên danh mục, Mô tả, Trạng thái)
                    .forEach(row -> {
                        result.setTotalRows(result.getTotalRows() + 1);
                        int rowNum = row.getRowNum();

                        CategoryImportDto dto = CategoryImportDto.builder()
                                .rowNum(rowNum)
                                .name(row.getCellText(1))
                                .description(row.getCellText(2))
                                .statusStr(row.getCellText(3)).build();

                        Set<ConstraintViolation<CategoryImportDto>> violations = validator.validate(dto);

                        if (!violations.isEmpty()) {
                            // Gom tất cả các lỗi của dòng này lại thành 1 chuỗi cách nhau bởi dấu phẩy
                            String combinedErrors = violations.stream()
                                    .map(ConstraintViolation::getMessage)
                                    .collect(Collectors.joining(", "));

                            RowErrorDetail newError = RowErrorDetail.builder()
                                    .rowIndex(rowNum)
                                    .errorMessage("[Lỗi] Bỏ qua dòng " + rowNum + " vì: " + combinedErrors)
                                    .build();
                            result.getErrors().add(newError);

                            return; // Bỏ qua dòng lỗi này, chuyển sang dòng tiếp theo
                        }

                        boolean finalStatus = Boolean.parseBoolean(dto.getStatusStr());

                        CategoryEntity newCategory = CategoryEntity.builder()
                                .id(UUID.randomUUID())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .isActive(finalStatus)
                                .rowNum(dto.getRowNum())
                                .build();

                        batchBuffer.add(newCategory);

                        if (batchBuffer.size() >= BATCH_SIZE) {
                            processBatch(batchBuffer, result);
                            batchBuffer.clear();
                        }
                    });

            if (!batchBuffer.isEmpty()) {
                processBatch(batchBuffer, result);
                batchBuffer.clear();
            }

            result.setErrorCount(result.getErrors().size());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi trong quá trình đọc file Excel: " + e.getMessage());
        }
    }

    private void processBatch(List<CategoryEntity> batchBuffer, ImportResultResponse result) {
        // Nguyên tắc: Các category không được trùng tên
        Set<String> namesInBatch = batchBuffer.stream().map(CategoryEntity::getName).collect(Collectors.toSet());

        // Gọi repository lấy ra các category bị trùng tên
        List<CategoryEntity> existingCategoryInNamesList = categoryRepository.findByNameIn(namesInBatch);
        Set<String> existingNames = existingCategoryInNamesList.stream().map(CategoryEntity::getName).collect(Collectors.toSet());

        // Lọc ra các category mới
        List<CategoryEntity> newCategories = new ArrayList<>();
        Set<String> newlyProcessedNames = new HashSet<>();

        for (CategoryEntity item : batchBuffer) {
            if (existingNames.contains(item.getName())) {
                int rowNum = item.getRowNum();
                RowErrorDetail errorDetail = RowErrorDetail.builder().rowIndex(rowNum).errorMessage("[Lỗi] Bỏ qua dòng " + rowNum + " vì danh mục '" + item.getName() + "' đã tồn tại trong hệ thống.").build();
                result.getErrors().add(errorDetail);
            } else if (newlyProcessedNames.contains(item.getName())) {
                int rowNum = item.getRowNum();
                RowErrorDetail errorDetail = RowErrorDetail.builder().rowIndex(rowNum).errorMessage("[Lỗi] Bỏ qua dòng " + rowNum + " vì danh mục '" + item.getName() + "' trùng lặp với danh mục đang import trong file.").build();
                result.getErrors().add(errorDetail);
            } else {
                newCategories.add(item);
                newlyProcessedNames.add(item.getName());
            }
        }

        // Chỉ insert các category mới
        if (!newCategories.isEmpty()) {
            List<CategoryEntity> saved = categoryRepository.saveAll(newCategories);

            // insert vào bảng outbox
            List<OutboxEventRequest> outboxRequests = saved.stream()
                .map(cat -> OutboxEventRequest.builder()
                    .eventType("CREATE")
                    .aggregateType("categories")
                    .aggregateId(cat.getId())
                    .payloadObj(cat)
                    .build()
                )
                .collect(Collectors.toList());
            outboxEventService.batchInsertOutboxEvents(outboxRequests);

            result.setSuccessCount(result.getSuccessCount() + saved.size());
            System.out.println("✅ Đã chèn thành công " + newCategories.size() + " danh mục mới.");
        }
    }

    private CategoryResponse mapToResponse(CategoryEntity entity) {
        return CategoryResponse.builder().id(entity.getId()).name(entity.getName()).description(entity.getDescription()).isActive(entity.isActive()).isDeleted(entity.isDeleted()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).version(entity.getVersion()).build();
    }
}
