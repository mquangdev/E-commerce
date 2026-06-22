package e_commerce.order_service.grpc;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.common_shared.grpc.catalog.CatalogGrpcServiceGrpc;
import e_commerce.common_shared.grpc.catalog.GetProductsRequest;
import e_commerce.common_shared.grpc.catalog.GetProductsResponse;
import e_commerce.common_shared.grpc.catalog.ProductInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogGrpcClientService {
  @GrpcClient("catalog-grpc-server")
  private CatalogGrpcServiceGrpc.CatalogGrpcServiceBlockingStub catalogStub;

  public Map<String, ProductInfo> fetchProductDetails(List<UUID> productIds) {
    log.info(
        "Order Service đang gọi gRPC sang Catalog để lấy thông tin {} sản phẩm", productIds.size());
    List<String> idsString = productIds.stream().map(uuid -> uuid.toString()).toList();
    GetProductsRequest request =
        GetProductsRequest.newBuilder().addAllProductIds(idsString).build();

    try {
      GetProductsResponse response = catalogStub.getProductsByIds(request);
      return response.getProductsMapMap();
    } catch (Exception e) {
      log.error("Lỗi khi gọi gRPC sang Catalog: {}", e.getMessage());
      throw new BusinessException(e.getMessage());
    }
  }
}
