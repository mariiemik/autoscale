# Autoscale Microservices

MSA система для обработки заказов, платежей, управления складом, пользователями, уведомлениями.

### 1. API Gateway (8080)

### 2. Order Service (8087/32087)

- `POST /order`
- `GET /order/{id}`
- `GET /order`

### 3. Payment Service (8082/32082)

- `POST /payment`
- `GET /payment/{id}`

### 4. External Payment Gateway (8083/32083)

- `POST /pay`

### 5. Inventory Service (8084/32084)

- `POST /inventory/reserve`
- `POST /inventory/cancel_reserve`
- `GET /inventory/{id}` — наличие товара
- `GET /inventory` — все доступные товары

### 6. User Service (8088/32088)

- `POST /user`
- `GET /user/{id}`

### 7. Notification Service (8085)

- `POST /notification`

### 8. Analytics Service (8089/32089)

- `GET /all` получить все метрики качества решения масштабируемых сервисов (user, payment, order, inventory)

## Полезные URL

- Eureka: `http://localhost:8761/`
- API Gateway: `http://localhost:8080/api/...`
- Swagger UI: `http://localhost:8088/swagger-ui.html`
- Prometheus metrics: `http://localhost:8088/actuator/prometheus`

![ER diagram](images/db.png)

## Логика событий

```mermaid

OrderService -->|OrderCreated| InventoryService, NotificationService
OrderService -->|OrderCancelled| InventoryService, NotificationService
OrderService -->|OrderConfirmed| InventoryService, NotificationService
InventoryService -->|ItemReserved| PaymentService
InventoryService -->|ItemReservationFailed| OrderService
PaymentService -->|PaymentStarted| OrderService
PaymentService -->|PaymentConfirmed| OrderService
PaymentService -->|PaymentFailed| OrderService, NotificationService

```

## Prometheus

Добавляем репозиторий Helm для Prometheus

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

Ставим kube-prometheus-stack

```bash
helm install prometheus prometheus-community/kube-prometheus-stack
```

Поднимаем Prometheus UI локально

```bash
kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090
```

Ставим Prometheus Adapter для HPA

```bash
helm install prometheus-adapter prometheus-community/prometheus-adapter
```

http://localhost:9090

Сделать кастомную конфигурацию для адаптера

```bash
helm upgrade prometheus-adapter prometheus-community/prometheus-adapter -f k8s/adapter-values.yaml
```

Установка Metrics Server для автоскейлинга по CPU/Memory

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.4/components.yaml
```

```bash
kubectl apply -f k8s/metrics-server.yaml
```

Grafana

```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
helm install grafana grafana/grafana
```

Смотрим пароль

```bash
kubectl get secret --namespace default grafana -o jsonpath="{.data.admin-password}" | ForEach-Object { [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_)) }
```

```
kubectl port-forward svc/grafana 3000:80
```

Открываем бразуер и заходим с данными
Логин: admin
Пароль: вставляем то, что получили, выполнив команду выше

во вкладке Data sources добавляем Prometheus
в поле url вставляем :

после перезагрузки
Prometheus

```bash
kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090
```

Grafana

```bash
kubectl port-forward svc/grafana 3000:80
```

kubectl port-forward svc/analytics-service 8089:8089
