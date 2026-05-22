---
title: "Backend API RESTful con Spring Boot y PostgreSQL - Arquitectura en Capas" 
date: 2025-11-06 
tags:
  - project-structure
  - architecture
  - backend
  - spring-boot
  - postgresql
  - layered-architecture 
stack:
  - Java 21+
  - Spring Boot 3.5
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Lombok
  - PostgreSQL 16
  - Maven/Gradle
  - Hibernate
  - Flyway/Liquibase
  - MapStruct 
principles:
  - "[[Layered Architecture]]"
  - "[[Separation of Concerns]]"
  - "[[Dependency Inversion Principle]]"
  - "[[Repository Pattern]]"
  - "[[SOLID Principles]]"
---
## 1. Philosophy & Guiding Principles

Esta estructura se basa en **[[Layered Architecture]]** (Arquitectura en Capas), un patrón probado y ampliamente adoptado para aplicaciones empresariales. El objetivo principal es lograr una **separación clara de responsabilidades** mediante capas horizontales que organizan el código según su función técnica.

- **Separation of Concerns:** Cada capa tiene una responsabilidad específica y bien definida.
- **Dependency Rule (Light):** Las capas superiores pueden depender de las inferiores, pero no al revés. Controller → Service → Repository → Database.
- **Pragmatic Simplicity:** No introduce complejidad innecesaria. Ideal para aplicaciones CRUD y APIs empresariales típicas.
- **Convention over Configuration:** Aprovecha las convenciones de Spring Boot para reducir configuración manual.
- **Testability:** Cada capa puede ser probada de forma independiente mediante interfaces y mocks.
---
## 2. Folder Structure Tree

```text
com.empresa.proyecto/
│
├── 📁 controller/              # Capa de Presentación (REST API)
│   ├── 📄 ClienteController.java
│   ├── 📄 ProductoController.java
│   ├── 📄 PedidoController.java
│   └── 📄 GlobalExceptionHandler.java
│
├── 📁 service/                 # Capa de Lógica de Negocio
│   ├── 📁 impl/
│   │   ├── 📄 ClienteServiceImpl.java
│   │   ├── 📄 ProductoServiceImpl.java
│   │   └── 📄 PedidoServiceImpl.java
│   ├── 📄 ClienteService.java
│   ├── 📄 ProductoService.java
│   └── 📄 PedidoService.java
│
├── 📁 repository/              # Capa de Acceso a Datos
│   ├── 📄 ClienteRepository.java
│   ├── 📄 ProductoRepository.java
│   └── 📄 PedidoRepository.java
│
├── 📁 model/                   # Entidades de Dominio (JPA Entities)
│   ├── 📄 Cliente.java
│   ├── 📄 Producto.java
│   ├── 📄 Pedido.java
│   └── 📄 ItemPedido.java
│
├── 📁 dto/                     # Data Transfer Objects
│   ├── 📁 request/
│   │   ├── 📄 ClienteRequest.java
│   │   ├── 📄 ProductoRequest.java
│   │   └── 📄 PedidoRequest.java
│   └── 📁 response/
│       ├── 📄 ClienteResponse.java
│       ├── 📄 ProductoResponse.java
│       ├── 📄 PedidoResponse.java
│       └── 📄 ApiResponse.java
│
├── 📁 mapper/                  # Conversión entre DTOs y Entities
│   ├── 📄 ClienteMapper.java
│   ├── 📄 ProductoMapper.java
│   └── 📄 PedidoMapper.java
│
├── 📁 config/                  # Configuraciones de Spring
│   ├── 📄 SecurityConfig.java
│   ├── 📄 CorsConfig.java
│   ├── 📄 OpenApiConfig.java
│   └── 📄 JpaConfig.java
│
├── 📁 exception/               # Manejo de Excepciones
│   ├── 📄 ResourceNotFoundException.java
│   ├── 📄 BadRequestException.java
│   ├── 📄 BusinessException.java
│   └── 📄 ErrorResponse.java
│
├── 📁 security/                # Seguridad y Autenticación
│   ├── 📄 JwtAuthenticationFilter.java
│   ├── 📄 JwtTokenProvider.java
│   ├── 📄 UserDetailsServiceImpl.java
│   └── 📄 SecurityUtils.java
│
├── 📁 util/                    # Utilidades y Helpers
│   ├── 📄 DateUtils.java
│   ├── 📄 ValidationUtils.java
│   └── 📄 Constants.java
│
└── 📄 Application.java         # Punto de entrada de la aplicación

resources/
├── 📄 application.yml          # Configuración principal
├── 📄 application-dev.yml      # Perfil de desarrollo
├── 📄 application-prod.yml     # Perfil de producción
└── 📁 db/
    └── 📁 migration/           # Scripts de Flyway/Liquibase
        ├── 📄 V1__create_clientes.sql
        ├── 📄 V2__create_productos.sql
        └── 📄 V3__create_pedidos.sql
```

## 3. Directory Breakdown

### **`/controller`**: Capa de Presentación (REST API)

Responsable de manejar las peticiones HTTP y devolver respuestas. Esta capa no contiene lógica de negocio.

**Responsabilidades:**

- Recibir y validar datos de entrada (DTOs de Request)
- Llamar a los servicios correspondientes
- **Recibir DTOs de Response directamente del servicio**
- Envolver respuestas en ApiResponse<T> para consistencia
- Manejar códigos de estado HTTP apropiados
- **NO realizar conversiones entre entidades y DTOs (lo hace el servicio)**

**Ejemplo de Controller:**
```java
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Validated
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> getAllClientes() {
        List<ClienteResponse> clientes = clienteService.findAll();
        return ResponseEntity.ok(ApiResponse.success(clientes));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> getClienteById(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> createCliente(
            @Valid @RequestBody ClienteRequest request) {
        ClienteResponse saved = clienteService.save(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Cliente creado exitosamente", saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> updateCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        ClienteResponse updated = clienteService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
    }
}
```

**Global Exception Handler:**

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .errors(errors)
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}
```

### **`/service`**: Capa de Lógica de Negocio

Contiene toda la lógica de negocio de la aplicación. Es el corazón del sistema.

**Responsabilidades:**

- Implementar reglas de negocio
- Coordinar operaciones entre múltiples repositorios
- Realizar validaciones complejas
- Manejar transacciones
- **Recibir DTOs de Request y retornar DTOs de Response**
- **Usar Mappers para convertir entre DTOs y Entidades**
- Mantener la separación entre la capa de presentación y la capa de persistencia

**Patrón Interface + Implementation:**

```java
// Interface
public interface ClienteService {
    List<ClienteResponse> findAll();
    ClienteResponse findById(Long id);
    ClienteResponse save(ClienteRequest request);
    ClienteResponse update(Long id, ClienteRequest request);
    void delete(Long id);
    boolean existsByEmail(String email);
    List<ClienteResponse> findByIngresoMinimo(BigDecimal ingresoMinimo);
}

// Implementation
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        log.debug("Fetching all clients");
        List<Cliente> clientes = clienteRepository.findAll();
        return clienteMapper.toResponseList(clientes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente not found with id: " + id));
        return clienteMapper.toResponse(cliente);
    }
    
    @Override
    public ClienteResponse save(ClienteRequest request) {
        // Validación de negocio
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        // Lógica de negocio adicional
        if (request.getIngresoMensual() != null && 
            request.getIngresoMensual().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Income cannot be negative");
        }
        
        // Convertir DTO a entidad
        Cliente cliente = clienteMapper.toEntity(request);
        
        log.info("Creating new client: {}", cliente.getNombreCompleto());
        Cliente saved = clienteRepository.save(cliente);
        
        // Convertir entidad a DTO para retornar
        return clienteMapper.toResponse(saved);
    }
    
    @Override
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente existing = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente not found with id: " + id));
        
        // Validar que el email no esté en uso por otro cliente
        if (!existing.getEmail().equals(request.getEmail()) && 
            existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        // Actualizar campos usando el mapper
        clienteMapper.updateEntityFromRequest(request, existing);
        
        log.info("Updating client with id: {}", id);
        Cliente updated = clienteRepository.save(existing);
        
        // Retornar DTO
        return clienteMapper.toResponse(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente not found with id: " + id);
        }
        
        log.info("Deleting client with id: {}", id);
        clienteRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> findByIngresoMinimo(BigDecimal ingresoMinimo) {
        List<Cliente> clientes = clienteRepository.findByIngresoMensualGreaterThanEqual(ingresoMinimo);
        return clienteMapper.toResponseList(clientes);
    }
}
```

**Service con lógica compleja (ejemplo de Pedidos):**

```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    
    @Override
    public Pedido crearPedido(Long clienteId, List<ItemPedidoRequest> items) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente not found with id: " + clienteId));
        
        // Validar y obtener productos
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        BigDecimal totalPedido = BigDecimal.ZERO;
        List<ItemPedido> itemsPedido = new ArrayList<>();
        
        for (ItemPedidoRequest itemRequest : items) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Producto not found with id: " + itemRequest.getProductoId()));
            
            // Validar stock
            if (producto.getStock() < itemRequest.getCantidad()) {
                throw new BusinessException(
                    "Insufficient stock for product: " + producto.getNombre());
            }
            
            // Crear item
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            
            BigDecimal subtotal = producto.getPrecio()
                .multiply(BigDecimal.valueOf(itemRequest.getCantidad()));
            item.setSubtotal(subtotal);
            
            itemsPedido.add(item);
            totalPedido = totalPedido.add(subtotal);
            
            // Actualizar stock
            producto.setStock(producto.getStock() - itemRequest.getCantidad());
            productoRepository.save(producto);
        }
        
        pedido.setItems(itemsPedido);
        pedido.setTotal(totalPedido);
        
        log.info("Creating order for client {} with total {}", clienteId, totalPedido);
        return pedidoRepository.save(pedido);
    }
    
    @Override
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Pedido not found with id: " + pedidoId));
        
        // Validar que se puede cancelar
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BusinessException("Cannot cancel delivered order");
        }
        
        // Restaurar stock
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        
        log.info("Order {} cancelled", pedidoId);
    }
}
```

### **`/repository`**: Capa de Acceso a Datos

Interfaces que extienden `JpaRepository` o `CrudRepository` para operaciones de base de datos.

**Responsabilidades:**

- Definir métodos de consulta personalizados
- Aprovechar query methods de Spring Data JPA
- Implementar consultas complejas con `@Query`

```java
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Query method (Spring Data JPA lo implementa automáticamente)
    Optional<Cliente> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Cliente> findByIngresoMensualGreaterThanEqual(BigDecimal ingresoMinimo);
    
    // Consulta personalizada con JPQL
    @Query("SELECT c FROM Cliente c WHERE c.nombreCompleto LIKE %:nombre%")
    List<Cliente> searchByNombre(@Param("nombre") String nombre);
    
    // Consulta nativa SQL
    @Query(value = "SELECT * FROM clientes WHERE dni = :dni", nativeQuery = true)
    Optional<Cliente> findByDniNative(@Param("dni") String dni);
    
    // Consulta con paginación
    Page<Cliente> findByOcupacion(String ocupacion, Pageable pageable);
}

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByStockLessThan(Integer minStock);
    
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minPrecio AND :maxPrecio")
    List<Producto> findByPrecioRange(
        @Param("minPrecio") BigDecimal minPrecio,
        @Param("maxPrecio") BigDecimal maxPrecio
    );
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombre")
    List<Producto> findAllActivos();
}

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteId(Long clienteId);
    
    List<Pedido> findByEstado(EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fecha BETWEEN :inicio AND :fin")
    List<Pedido> findByFechaRange(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = :estado")
    BigDecimal calculateTotalByEstado(@Param("estado") EstadoPedido estado);
}
```

### **`/model`**: Entidades JPA

Clases que representan las tablas de la base de datos.

```java
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;
    
    @Column(unique = true, nullable = false, length = 8)
    private String dni;
    
    @Column(length = 15)
    private String telefono;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "ingreso_mensual", precision = 10, scale = 2)
    private BigDecimal ingresoMensual;
    
    @Column(length = 100)
    private String ocupacion;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(length = 100)
    private String categoria;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}

@Entity
@Table(name = "items_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    EN_PREPARACION,
    ENVIADO,
    ENTREGADO,
    CANCELADO
}
```

### **`/dto`**: Data Transfer Objects

Clases para transferir datos entre capas, separadas en request y response.

```java
// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequest {
    
    @NotBlank(message = "Nombre completo es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombreCompleto;
    
    @NotBlank(message = "DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "DNI debe tener 8 dígitos")
    private String dni;
    
    @NotBlank(message = "Teléfono es obligatorio")
    @Pattern(regexp = "\\d{9,15}", message = "Teléfono inválido")
    private String telefono;
    
    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Email debe ser válido")
    private String email;
    
    @DecimalMin(value = "0.0", message = "Ingreso no puede ser negativo")
    private BigDecimal ingresoMensual;
    
    @Size(max = 100, message = "Ocupación no puede exceder 100 caracteres")
    private String ocupacion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {
    
    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 200)
    private String nombre;
    
    @Size(max = 500)
    private String descripcion;
    
    @NotNull(message = "Precio es obligatorio")
    @DecimalMin(value = "0.01", message = "Precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @NotNull(message = "Stock es obligatorio")
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stock;
    
    private String categoria;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    
    @NotNull(message = "Cliente ID es obligatorio")
    private Long clienteId;
    
    @NotEmpty(message = "Pedido debe tener al menos un item")
    private List<ItemPedidoRequest> items;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequest {
    
    @NotNull(message = "Producto ID es obligatorio")
    private Long productoId;
    
    @NotNull(message = "Cantidad es obligatoria")
    @Min(value = 1, message = "Cantidad debe ser al menos 1")
    private Integer cantidad;
}

// Response DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private String email;
    private BigDecimal ingresoMensual;
    private String ocupacion;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String categoria;
    private Boolean activo;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private Long id;
    private ClienteResponse cliente;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private BigDecimal total;
    private List<ItemPedidoResponse> items;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoResponse {
    private Long id;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}

// Generic API Response
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### **`/mapper`**: Conversión entre DTOs y Entities

Usando MapStruct para conversiones automáticas y type-safe.

```java
@Mapper(componentModel = "spring")
public interface ClienteMapper {
    
    ClienteResponse toResponse(Cliente cliente);
    
    List<ClienteResponse> toResponseList(List<Cliente> clientes);
    
    Cliente toEntity(ClienteRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    void updateEntityFromRequest(ClienteRequest request, @MappingTarget Cliente cliente);
}

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    
    ProductoResponse toResponse(Producto producto);
    
    List<ProductoResponse> toResponseList(List<Producto> productos);
    
    Producto toEntity(ProductoRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntityFromRequest(ProductoRequest request, @MappingTarget Producto producto);
}

@Mapper(componentModel = "spring", uses = {ClienteMapper.class})
public interface PedidoMapper {
    
    @Mapping(source = "items", target = "items")
    PedidoResponse toResponse(Pedido pedido);
    
    List<PedidoResponse> toResponseList(List<Pedido> pedidos);
    
    @Mapping(source = "producto.nombre", target = "productoNombre")
    ItemPedidoResponse itemToResponse(ItemPedido item);
}
```

### **`/config`**: Base Spring Configurations

#### SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```

#### OpenApiConfig
```java
@Configuration 
public class OpenApiConfig {
	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	        .info(new Info()
	            .title("API RESTful - Sistema de Gestión")
	            .version("1.0")
	            .description("API para gestión de clientes, productos y pedidos")
	            .contact(new Contact()
	                .name("Equipo de Desarrollo")
	                .email("dev@empresa.com")
	            )
	        )
	        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
	        .components(new Components()
	            .addSecuritySchemes("Bearer Authentication", 
	                new SecurityScheme()
	                    .type(SecurityScheme.Type.HTTP)
	                    .scheme("bearer")
	                    .bearerFormat("JWT")
	            )
	        );
	}
	
	@Bean
	public GroupedOpenApi publicApi() {
	    return GroupedOpenApi.builder()
	        .group("public")
	        .pathsToMatch("/api/v1/**")
	        .build();
	}
}
```

#### JpaConfig
```java
@Configuration 
@EnableJpaAuditing 
public class JpaConfig {
	@Bean
	public AuditorAware<String> auditorProvider() {
	    return () -> Optional.of("system"); // O extraer del contexto de seguridad
	}
}
```

#### CorsConfig
```java
@Configuration 
public class CorsConfig {
	@Bean
	public CorsFilter corsFilter() {
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOriginPattern("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
	    source.registerCorsConfiguration("/api/**", config);
	    return new CorsFilter(source);
	}
}
```

### **`/exception`**: Manejo de Excepciones
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
```

### **`/security`**: Seguridad y JWT
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;
    
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
}

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getActive())
            .build();
    }
}

@Component
public class SecurityUtils {
    
    public static Optional<String> getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            });
    }
    
    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
```

### **`/util`**: Utilidades
```java
public class DateUtils {
    
    public static LocalDateTime parseToLocalDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }
    
    public static String formatToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
    
    public static boolean isBetween(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
    
    public static boolean isValidDNI(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{9,15}");
    }
    
    public static String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("\\s+", " ");
    }
}

public class Constants {
    
    // JWT
    public static final String JWT_SECRET = "mySecretKey";
    public static final long JWT_EXPIRATION = 86400000; // 24 horas
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Business Rules
    public static final BigDecimal MIN_INGRESO_MENSUAL = new BigDecimal("930.00"); // Sueldo mínimo Perú
    public static final int MIN_STOCK_ALERTA = 10;
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // API
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
}
```

---

## 4. Data Flow & Architecture Patterns

### Request Flow (Client → Database)
```
HTTP Request (JSON) 
↓ 
┌──────────────────┐ 
│    Controller    │  - Recibe request    
│  (Presentation)  │  - Valida @Valid     
│                  │  - Pasa Request DTO al servicio      
└──────────────────┘ 
	     ↓ 
┌──────────────────┐ 
│     Service      │  - Recibe Request DTO
│ (Business Logic) │  - Usa Mapper para convertir DTO → Entity
│                  │  - Ejecuta lógica de negocio 
│                  │  - Valida reglas de negocio  
│                  │  - Coordina múltiples repositorios  
│                  │  - Maneja @Transactional
│                  │  - Usa Mapper para convertir Entity → Response DTO
│                  │  - Retorna Response DTO
└──────────────────┘
         ↓ 
┌──────────────────┐ 
│    Repository    │  - Trabaja solo con Entities
│  (Data Access)   │  - Define contrato de datos 
│                  │  - Query methods / @Query 
└──────────────────┘ 
         ↓ 
┌──────────────────┐ 
│  JPA/Hibernate   │  - ORM 
│                  │  - Genera SQL 
└──────────────────┘ 
         ↓ 
┌──────────────────┐ 
│    PostgreSQL    │  - Almacena datos 
└──────────────────┘
```

### Response Flow (Database → Client)
```
PostgreSQL 
↓ 
JPA/Hibernate (mapea ResultSet → Entity) 
↓ 
Repository (retorna Entity) 
↓ 
Mapper (convierte Entity → DTO) 
↓ 
Service (aplica lógica, retorna DTO) 
↓ 
Controller (recibe DTO, envuelve en ApiResponse<T>) 
↓ 
HTTP Response (JSON serializado por Jackson)
```

### Flujo de Transacción Compleja (Ejemplo: Crear Pedido)

```text
1. Cliente → POST /api/v1/pedidos con PedidoRequest (DTO)
2. PedidoController.crearPedido(PedidoRequest) ↓
3. PedidoService.crearPedido(PedidoRequest) [@Transactional] 
   ├─→ Mapper: PedidoRequest → Pedido Entity
   ├─→ ClienteRepository.findById() → Validar cliente existe 
   ├─→ ProductoRepository.findById() → Validar productos existen 
   ├─→ Validar stock suficiente 
   ├─→ Calcular subtotales y total 
   ├─→ ProductoRepository.save() → Actualizar stock 
   ├─→ PedidoRepository.save() → Guardar pedido con items
   └─→ Mapper: Pedido Entity → PedidoResponse (DTO)
4.  Si hay excepción → Rollback automático
5.  Si success → Commit y retornar PedidoResponse
6. Controller → Envuelve en ApiResponse<PedidoResponse>
7. Controller → ResponseEntity<ApiResponse<PedidoResponse>>
```

### Key Patterns Applied

1. **[[Layered Architecture]]**: Separación en capas horizontales (Controller, Service, Repository)
2. **[[What is exactly the Repository Pattern about]]**: Abstracción de acceso a datos mediante Spring Data JPA
3. **[[Data Transfer Object (DTO)]]**: Separación entre representación externa (API) e interna (Entity)
4. **[[Dependency Injection]]**: Spring IoC Container maneja todas las dependencias
5. **[[Mapper Pattern]]**: MapStruct para conversión type-safe entre DTOs y Entities
6. **[[Service Layer Pattern]]**: Lógica de negocio encapsulada en servicios reutilizables que retornan DTOs
7. **[[Transaction Script]]**: Cada método de servicio maneja una transacción completa

### Ventajas de Retornar DTOs desde los Servicios

**✅ Encapsulación y Seguridad:**
- Los servicios no exponen las entidades JPA directamente
- Se evita la filtración accidental de datos sensibles (passwords, campos internos)
- Control total sobre qué datos se exponen en cada endpoint

**✅ Desacoplamiento:**
- Cambios en la estructura de la base de datos no afectan a los consumidores de la API
- La capa de presentación no depende de las entidades de persistencia
- Facilita la evolución independiente de cada capa

**✅ Flexibilidad:**
- Los DTOs pueden incluir campos calculados o agregaciones
- Diferentes vistas de los mismos datos (ej: ClienteSummary, ClienteDetailed)
- Adaptación fácil a requisitos específicos del frontend

**✅ Prevención de Problemas de Lazy Loading:**
- Los DTOs son objetos simples sin proxies de Hibernate
- No hay riesgo de LazyInitializationException
- Performance predecible sin cargas inesperadas

**✅ Testabilidad:**
- Los servicios se pueden testear sin necesidad de contexto de persistencia
- DTOs son simples POJOs fáciles de crear en tests
- Mocks más simples y claros

**✅ Documentación de API Clara:**
- Los DTOs definen claramente el contrato de la API
- Herramientas como Swagger/OpenAPI generan documentación precisa
- Los consumidores saben exactamente qué esperar

**✅ Versionado de API Simplificado:**
- Diferentes versiones de DTOs para diferentes versiones de la API
- Sin impacto en las entidades de dominio

---
## 5. Configuration Files

### application.yml

```yaml
spring:
  application:
    name: api-restful-base
  
  datasource:
    url: jdbc:postgresql://localhost:5432/proyecto_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate  # validate, update, create, create-drop
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    open-in-view: false
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
  
  jackson:
    serialization:
      write-dates-as-timestamps: false
    time-zone: America/Lima
    default-property-inclusion: non_null

# JWT Configuration
app:
  jwt:
    secret: ${JWT_SECRET:myVerySecretKeyForJWTTokenGeneration2024}
    expiration: 86400000  # 24 horas en milisegundos

# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /
  error:
    include-message: always
    include-binding-errors: always

# Logging
logging:
  level:
    root: INFO
    com.empresa.proyecto: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30

# Springdoc OpenAPI
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha

# Management Endpoints (Actuator)
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### application-dev.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/proyecto_db_dev
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: DEBUG
    com.empresa.proyecto: TRACE

server:
  port: 8081
```

### application-prod.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    root: WARN
    com.empresa.proyecto: INFO

server:
  port: ${PORT:8080}

app:
  jwt:
  secret: ${JWT_SECRET}
```

### pom.xml (Maven)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.empresa</groupId>
    <artifactId>proyecto-api</artifactId>
    <version>1.0.0</version>
    <name>API RESTful Base</name>
    <description>Backend API con Spring Boot y PostgreSQL</description>
    
    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <lombok.version>1.18.30</lombok.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        
        <!-- OpenAPI / Swagger -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


### Flyway Migration Example

```sql
-- db/migration/V1__create_clientes.sql
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(200) NOT NULL,
    dni VARCHAR(8) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(255) UNIQUE NOT NULL,
    ingreso_mensual DECIMAL(10, 2),
    ocupacion VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_dni ON clientes(dni);

-- db/migration/V2__create_productos.sql
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    categoria VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_nombre ON productos(nombre);

-- db/migration/V3__create_pedidos.sql
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE items_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_items_pedido ON items_pedido(pedido_id);
```

---

## 6. Key Trade-offs

### Pros

- ✅ **Simplicidad y Familiaridad**: Arquitectura ampliamente conocida, fácil de entender para nuevos desarrolladores
- ✅ **Rápido Desarrollo Inicial**: Spring Boot + JPA permiten crear CRUDs muy rápidamente
- ✅ **Convención sobre Configuración**: Spring Boot reduce configuración manual
- ✅ **Ecosistema Maduro**: Abundante documentación, librerías y comunidad
- ✅ **Testabilidad Buena**: Cada capa puede ser testeada con mocks
- ✅ **Transacciones Declarativas**: `@Transactional` simplifica el manejo de transacciones
- ✅ **ORM Potente**: Hibernate/JPA reduce código SQL manual
- ✅ **Ideal para CRUD**: Perfecto para aplicaciones empresariales típicas

### Cons

- ❌ **Acoplamiento a Framework**: Fuerte dependencia de Spring y JPA
- ❌ **Dominio Anémico**: Entidades JPA suelen convertirse en clases con solo getters/setters
- ❌ **Complejidad en Lógica Compleja**: Service Layer puede volverse un "God Object" con muchas responsabilidades
- ❌ **Organización Técnica vs Funcional**: Dificulta encontrar toda la lógica de una funcionalidad (está dispersa en capas)
- ❌ **Transacciones por defecto**: `@Transactional` a nivel de servicio puede generar sesiones largas de Hibernate
- ❌ **Performance**: ORM puede generar queries no optimizadas (N+1 problem)
- ❌ **Testing de Integración Lento**: Tests con Spring Context son lentos de ejecutar
- ❌ **Escalabilidad Horizontal Limitada**: Al ser monolítico, escalar requiere replicar toda la aplicación

---

## 7. When to Use This Structure

### ✅ Use this structure when:

- Construyendo aplicaciones CRUD empresariales tradicionales
- El equipo está familiarizado con Spring Boot y JPA
- Necesitas desarrollar un MVP rápidamente
- La lógica de negocio no es extremadamente compleja
- El proyecto es de tamaño pequeño a mediano (< 50 entidades)
- Trabajas en una empresa con estándares establecidos en Spring
- Necesitas aprovechar el ecosistema de Spring (Security, Cloud, etc.)
- El tiempo de salida al mercado (time-to-market) es crítico
- Tienes requisitos típicos de aplicaciones empresariales (autenticación, auditoría, transacciones)

### ❌ Consider simpler alternatives when:

- Construyendo un microservicio muy pequeño y específico → **Considera arquitectura más ligera**
- La lógica de negocio es extremadamente compleja → **Considera Clean Architecture o DDD**
- Necesitas independencia total del framework → **Considera Hexagonal Architecture**
- Trabajas en un contexto de alta escalabilidad → **Considera Event-Driven Architecture o CQRS**
- El equipo es junior y Spring puede ser abrumador → **Considera frameworks más simples**

---

## 8. Testing Strategy

### Unit Tests

**Service Layer (Business Logic)**
```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @InjectMocks
    private ClienteServiceImpl clienteService;
    
    @Test
    void findById_ClienteExists_ReturnsCliente() {
        // Arrange
        Long id = 1L;
        Cliente cliente = Cliente.builder()
            .id(id)
            .nombreCompleto("Juan Pérez")
            .email("juan@example.com")
            .build();
        
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        
        // Act
        Cliente result = clienteService.findById(id);
        
        // Assert
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getNombreCompleto());
        verify(clienteRepository, times(1)).findById(id);
    }
    
    @Test
    void findById_ClienteNotExists_ThrowsException() {
    // Arrange
        Long id = 999L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.findById(id);
        });
        
        verify(clienteRepository, times(1)).findById(id);
    }
    
    @Test
    void save_ValidCliente_ReturnsCreatedCliente() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .nombreCompleto("María García")
            .dni("12345678")
            .email("maria@example.com")
            .ingresoMensual(new BigDecimal("2000.00"))
            .build();
        
        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // Act
        Cliente result = clienteService.save(cliente);
        
        // Assert
        assertNotNull(result);
        assertEquals("María García", result.getNombreCompleto());
        verify(clienteRepository).existsByEmail(cliente.getEmail());
        verify(clienteRepository).save(cliente);
    }
    
    @Test
    void save_DuplicateEmail_ThrowsBusinessException() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .email("existing@example.com")
            .build();
        
        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            clienteService.save(cliente);
        });
        
        verify(clienteRepository).existsByEmail(cliente.getEmail());
        verify(clienteRepository, never()).save(any());
    }
    
    @Test
    void save_NegativeIncome_ThrowsBusinessException() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .email("test@example.com")
            .ingresoMensual(new BigDecimal("-1000.00"))
            .build();
        
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            clienteService.save(cliente);
        });
    }
}
```

**Controller Layer (API Endpoints)**
```java
@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
class ClienteControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ClienteService clienteService;
    
    @MockBean
    private ClienteMapper clienteMapper;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    private UserDetailsService userDetailsService;
    
    @Test
    @WithMockUser
    void getAllClientes_ReturnsListOfClientes() throws Exception {
        // Arrange
        List<Cliente> clientes = Arrays.asList(
            Cliente.builder().id(1L).nombreCompleto("Juan Pérez").build(),
            Cliente.builder().id(2L).nombreCompleto("María García").build()
        );
        
        List<ClienteResponse> responses = Arrays.asList(
            ClienteResponse.builder().id(1L).nombreCompleto("Juan Pérez").build(),
            ClienteResponse.builder().id(2L).nombreCompleto("María García").build()
        );
        
        when(clienteService.findAll()).thenReturn(clientes);
        when(clienteMapper.toResponse(any())).thenReturn(responses.get(0), responses.get(1));
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreCompleto").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombreCompleto").value("María García"));
        
        verify(clienteService).findAll();
    }
    
    @Test
    @WithMockUser
    void getClienteById_ClienteExists_ReturnsCliente() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder()
            .id(1L)
            .nombreCompleto("Juan Pérez")
            .email("juan@example.com")
            .build();
        
        ClienteResponse response = ClienteResponse.builder()
            .id(1L)
            .nombreCompleto("Juan Pérez")
            .email("juan@example.com")
            .build();
        
        when(clienteService.findById(1L)).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }
    
    @Test
    @WithMockUser
    void getClienteById_ClienteNotFound_Returns404() throws Exception {
        // Arrange
        when(clienteService.findById(999L))
            .thenThrow(new ResourceNotFoundException("Cliente not found"));
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente not found"));
    }
    
    @Test
    @WithMockUser
    void createCliente_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        ClienteRequest request = ClienteRequest.builder()
            .nombreCompleto("Nuevo Cliente")
            .dni("12345678")
            .email("nuevo@example.com")
            .telefono("987654321")
            .ingresoMensual(new BigDecimal("3000.00"))
            .build();
        
        Cliente cliente = Cliente.builder()
            .id(1L)
            .nombreCompleto("Nuevo Cliente")
            .build();
        
        ClienteResponse response = ClienteResponse.builder()
            .id(1L)
            .nombreCompleto("Nuevo Cliente")
            .build();
        
        when(clienteMapper.toEntity(any(ClienteRequest.class))).thenReturn(cliente);
        when(clienteService.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponse(any(Cliente.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Nuevo Cliente"));
    }
    
    @Test
    @WithMockUser
    void createCliente_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange - Request sin campos requeridos
        ClienteRequest request = ClienteRequest.builder().build();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
```

### Integration Tests

**Repository Layer**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ClienteRepositoryTest {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void findByEmail_EmailExists_ReturnsCliente() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .nombreCompleto("Test User")
            .dni("12345678")
            .email("test@example.com")
            .telefono("987654321")
            .build();
        
        entityManager.persistAndFlush(cliente);
        
        // Act
        Optional<Cliente> found = clienteRepository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getNombreCompleto());
    }
    
    @Test
    void findByEmail_EmailNotExists_ReturnsEmpty() {
        // Act
        Optional<Cliente> found = clienteRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(found.isPresent());
    }
    
    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .nombreCompleto("Test User")
            .dni("12345678")
            .email("exists@example.com")
            .build();
        
        entityManager.persistAndFlush(cliente);
        
        // Act
        boolean exists = clienteRepository.existsByEmail("exists@example.com");
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void findByIngresoMensualGreaterThanEqual_ReturnsMatchingClientes() {
        // Arrange
        Cliente cliente1 = Cliente.builder()
            .nombreCompleto("Cliente 1")
            .dni("11111111")
            .email("cliente1@example.com")
            .ingresoMensual(new BigDecimal("2000.00"))
            .build();
        
        Cliente cliente2 = Cliente.builder()
            .nombreCompleto("Cliente 2")
            .dni("22222222")
            .email("cliente2@example.com")
            .ingresoMensual(new BigDecimal("5000.00"))
            .build();
        
        Cliente cliente3 = Cliente.builder()
            .nombreCompleto("Cliente 3")
            .dni("33333333")
            .email("cliente3@example.com")
            .ingresoMensual(new BigDecimal("1500.00"))
            .build();
        
        entityManager.persist(cliente1);
        entityManager.persist(cliente2);
        entityManager.persist(cliente3);
        entityManager.flush();
        
        // Act
        List<Cliente> result = clienteRepository
            .findByIngresoMensualGreaterThanEqual(new BigDecimal("2000.00"));
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream()
            .allMatch(c -> c.getIngresoMensual().compareTo(new BigDecimal("2000.00")) >= 0));
    }
}
```

**Service Integration Test (con TestContainers)**
```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ClienteServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }
    
    @Test
    void createAndRetrieveCliente_Success() {
        // Arrange
        ClienteRequest request = ClienteRequest.builder()
            .nombreCompleto("Integration Test Cliente")
            .dni("99999999")
            .email("integration@test.com")
            .telefono("999888777")
            .ingresoMensual(new BigDecimal("4000.00"))
            .ocupacion("QA Engineer")
            .build();
        
        ResponseEntity<ClienteResponse> createResponse = restTemplate
            .withBasicAuth("admin", "admin") // o usar JWT token
            .postForEntity(baseUrl, request, ClienteResponse.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long clienteId = createResponse.getBody().getId();
        
        // 2. Read
        ResponseEntity<ClienteResponse> getResponse = restTemplate
            .withBasicAuth("admin", "admin")
            .getForEntity(baseUrl + "/" + clienteId, ClienteResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Cliente", getResponse.getBody().getNombreCompleto());
        
        // 3. Update
        ClienteRequest updateRequest = ClienteRequest.builder()
            .nombreCompleto("Updated E2E Cliente")
            .dni("99999999")
            .email("integration@test.com")
            .telefono("999999999")
            .ingresoMensual(new BigDecimal("5000.00"))
            .build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClienteRequest> updateEntity = new HttpEntity<>(updateRequest, headers);
        
        ResponseEntity<ClienteResponse> updateResponse = restTemplate
            .withBasicAuth("admin", "admin")
            .exchange(baseUrl + "/" + clienteId, HttpMethod.PUT, 
                     updateEntity, ClienteResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated E2E Cliente", updateResponse.getBody().getNombreCompleto());
        
        // 4. Delete
        restTemplate
            .withBasicAuth("admin", "admin")
            .delete(baseUrl + "/" + clienteId);
        
        ResponseEntity<ClienteResponse> deletedResponse = restTemplate
            .withBasicAuth("admin", "admin")
            .getForEntity(baseUrl + "/" + clienteId, ClienteResponse.class);
        
        assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
    }
}
```

## Correo HTML con estilo

Para enviar correos con diseño HTML en este proyecto:

1. `pom.xml` incluye `spring-boot-starter-mail` y `spring-boot-starter-thymeleaf`.
2. La plantilla debe estar en `src/main/resources/templates/email.html`.
3. El servicio `EmailServiceImpl` renderiza la plantilla con `TemplateEngine` y envía un `MimeMessage`.
4. Los datos dinámicos que usa la plantilla son:
   - `nombre`
   - `codigo`
   - `minutos`
   - `anio`
   - `email`
   - `url_base`
   - `url_ayuda`
   - `url_privacidad`
   - `url_terminos`
   - `brand_name`
5. La configuración SMTP se toma desde `application.properties`:
   - `spring.mail.host`
   - `spring.mail.port`
   - `spring.mail.username`
   - `spring.mail.password`
   - `app.mail.from`
   - `app.mail.otp-subject`

### Recomendaciones

- Usa variables de entorno para credenciales SMTP.
- Mantén el HTML compatible con clientes de correo: evita depender de CSS muy avanzado.
- Si cambias el nombre de la plantilla, actualiza el nombre usado en `templateEngine.process(...)`.

## 9. Related Concepts

- [[Layered Architecture]]
- [[Separation of Concerns]]
- [[Repository Pattern]]
- [[Data Transfer Object (DTO)]]
- [[Dependency Injection]]
- [[Service Layer Pattern]]
- [[SOLID Principles]]
- [[Dependency Inversion Principle]]
- [[Transaction Script Pattern]]
- [[Object-Relational Mapping (ORM)]]
- [[RESTful API Design]]
- [[Spring Framework Architecture]]
- [[JPA Entity Lifecycle]]
- [[Database Migration Strategies]]
- [[API Versioning]]

## 10. Additional Resources

### Official Documentation

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

### Recommended Libraries/Tools

- **Lombok**: Reduce boilerplate code con anotaciones
- **MapStruct**: Conversión type-safe entre DTOs y Entities
- **Springdoc OpenAPI**: Documentación automática de API REST
- **Flyway/Liquibase**: Migración y versionado de base de datos
- **TestContainers**: Tests de integración con contenedores Docker
- **JUnit 5**: Framework de testing moderno
- **Mockito**: Librería de mocking para unit tests
- **RestAssured**: Testing de APIs REST
- **Hibernate Validator**: Validación de beans con anotaciones

### Books & Articles

- "Spring in Action" by Craig Walls
- "Pro Spring 5" by Iuliana Cosmina
- "Java Persistence with Spring Data and Hibernate" by Catalin Tudose
- "RESTful Web Services" by Leonard Richardson
- [Baeldung - Spring Boot Tutorials](https://www.baeldung.com/spring-boot)
- [Spring Blog - Best Practices](https://spring.io/blog)

### Community Examples

- [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) - Aplicación de referencia oficial
- [Real World Spring Boot](https://github.com/gothinkster/spring-boot-realworld-example-app)
- [Spring Boot Best Practices](https://github.com/yingvickycao/spring-boot-best-practices)

---

## 11. Migration Notes

### From Legacy Spring (XML Config) to Spring Boot

**Phase 1: Setup (Week 1)**

1. Crear nuevo proyecto Spring Boot usando [Spring Initializr](https://start.spring.io/)
2. Migrar dependencias de Maven/Gradle
3. Convertir configuraciones XML a `@Configuration` classes o `application.yml`

**Phase 2: Core Migration (Weeks 2-4)**

1. Migrar beans XML a anotaciones `@Component`, `@Service`, `@Repository`
2. Reemplazar `web.xml` con configuración embedded server
3. Migrar DataSource configuration a `application.yml`
4. Actualizar Spring Security config de XML a Java Config

**Phase 3: Testing & Refinement (Week 5+)**

1. Migrar tests a JUnit 5 y Spring Boot Test
2. Implementar Actuator para monitoring
3. Añadir OpenAPI documentation
4. Configurar profiles (dev, prod)

### From Monolithic Service Layer to Structured Layered

**Week 1-2: Reorganize Structure**

1. Crear estructura de carpetas (controller, service, repository, model, dto)
2. Identificar clases existentes y clasificarlas por capa
3. Mover archivos a sus carpetas correspondientes

**Week 3-4: Extract DTOs**

1. Crear DTOs separados de Entities
2. Implementar Mappers con MapStruct
3. Actualizar Controllers para usar DTOs en lugar de Entities

**Week 5-6: Refactor Services**

1. Separar lógica de negocio compleja en métodos pequeños
2. Extraer validaciones a métodos dedicados
3. Implementar pattern Interface + Implementation para Services

**Week 7+: Add Cross-Cutting Concerns**

1. Implementar Global Exception Handler
2. Añadir logging estratégico
3. Configurar transacciones apropiadamente
4. Añadir validaciones con Bean Validation

### Best Practices During Migration

- **Test Everything**: Mantener cobertura de tests durante migración
- **Incremental Changes**: No migrar todo a la vez, hacerlo por módulos
- **Backward Compatibility**: Mantener APIs existentes funcionando
- **Documentation**: Documentar cambios y decisiones arquitectónicas
- **Code Reviews**: Revisar cada cambio con el equipo
- **Performance Monitoring**: Comparar performance antes/después

