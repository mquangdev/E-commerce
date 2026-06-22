package e_commerce.catalog_service.grpc;

import e_commerce.catalog_service.commands.entities.ProductEntity;
import e_commerce.catalog_service.commands.repositories.ProductRepository;
import e_commerce.common_shared.grpc.catalog.CatalogGrpcServiceGrpc;
import e_commerce.common_shared.grpc.catalog.GetProductsRequest;
import e_commerce.common_shared.grpc.catalog.GetProductsResponse;
import e_commerce.common_shared.grpc.catalog.ProductInfo;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CatalogGrpcServceImpl extends CatalogGrpcServiceGrpc.CatalogGrpcServiceImplBase {
  private final ProductRepository productRepository;

  @Override
  public void getProductsByIds(
      GetProductsRequest request, StreamObserver<GetProductsResponse> responseObserver) {

    try {
      GetProductsResponse.Builder responseBuilder = GetProductsResponse.newBuilder();
      List<UUID> uuidList =
          request.getProductIdsList().stream()
              .map(
                  idStr -> {
                    try {
                      return UUID.fromString(idStr);
                    } catch (Exception e) {
                      log.warn("gRPC Request có chứa UUID không hợp lệ: {}", idStr);
                      return null;
                    }
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      if (!uuidList.isEmpty()) {
        List<ProductEntity> products = productRepository.findAllByIdInAndIsDeletedFalse(uuidList);

        for (ProductEntity product : products) {
          ProductInfo productInfo =
              ProductInfo.newBuilder()
                  .setProductId(product.getId().toString())
                  .setName(product.getName())
                  .setImageUrl(product.getImageUrl())
                  .setProductId(product.getId().toString())
                  .build();

          responseBuilder.putProductsMap(product.getId().toString(), productInfo);
        }
      }

      responseObserver.onNext(responseBuilder.build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Lỗi khi lấy danh sách sản phẩm qua gRPC: ", e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Lỗi hệ thống khi lấy sản phẩm: " + e.getMessage())
              .asRuntimeException());
    }
  }
}
