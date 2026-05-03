import http from 'k6/http';
import { sleep } from 'k6';
import { check } from 'k6';

export const options = {
  vus: 5,          // 50 "пользователей"
  duration: '1m',   // 2 минуты нагрузки
};

function randomIndex(max) {
  return Math.floor(Math.random() * max);
}

function randomInt(max) {
  return Math.floor(Math.random() * max) + 1;
}

export default function () {
  // 1. получаем список товаров
//  let productsRes = http.get('http://localhost:8084/inventory');
  let productsRes = http.get('http://inventory-service:8084/inventory');
    let products;
    try {
      products = JSON.parse(productsRes.body);
    } catch (e) {
      return;
    }
  if (!products || products.length === 0) {
    return;
  }
  check(productsRes, {
    'products loaded': (r) => r.status === 200,
  });

    if (productsRes.status !== 200) return;

  // 2. выбираем случайный товар
    let product = products[randomIndex(products.length)];

  // 3. создаём заказ
   let orderRes = http.post(
//     'http://localhost:8087/orders',
     'http://order-service:8087/orders',
     JSON.stringify({
       userId: "1",
       items: [
         {
           itemId: String(product.inventoryItemId),
           quantity: randomInt(3),
           name: product.name,
           price: product.price
         }
       ]
     }),
     { headers: { 'Content-Type': 'application/json' } }
   );

    check(orderRes, {
      'order created': (r) => r.status === 200 || r.status === 201,
    });

    if (orderRes.status !== 200 && orderRes.status !== 201) return;


  // допустим, API возвращает id заказа
let order;
try {
  order = JSON.parse(orderRes.body);
} catch (e) {
  return;
}
  // 3. несколько проверок заказа
  let checks = randomInt(3); // 1–3 раза

  for (let i = 0; i < checks; i++) {
//    http.get(`http://order-service:8087/orders/details/${order.orderId}`);
//    http.get(`http://order-service:8087/orders/details/${order.orderId}`);
    http.get(
      "http://order-service:8087/orders/details/" + order.orderId
    );
  }

  sleep(1);
}


