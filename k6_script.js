import http from 'k6/http';
import { sleep } from 'k6';

// Test senaryolarının ayarları
export const options = {
  scenarios: {
    // BUY API Testleri
    buy_20: { executor: 'constant-vus', vus: 20, duration: '30s', exec: 'buyTest', startTime: '0s' },
    buy_50: { executor: 'constant-vus', vus: 50, duration: '30s', exec: 'buyTest', startTime: '35s' },
    buy_100: { executor: 'constant-vus', vus: 100, duration: '30s', exec: 'buyTest', startTime: '70s' },

    // CHECK-IN API Testleri
    checkin_20: { executor: 'constant-vus', vus: 20, duration: '30s', exec: 'checkinTest', startTime: '105s' },
    checkin_50: { executor: 'constant-vus', vus: 50, duration: '30s', exec: 'checkinTest', startTime: '140s' },
    checkin_100: { executor: 'constant-vus', vus: 100, duration: '30s', exec: 'checkinTest', startTime: '175s' },
  },
};

// 1. BUY API İsteği
export function buyTest() {
  const url = 'http://localhost:8080/api/buy';
  const payload = JSON.stringify({ productId: 1, userId: 123 });
  const params = { headers: { 'Content-Type': 'application/json' } };

  http.post(url, payload, params);
}

// 2. CHECK-IN API İsteği
export function checkinTest() {
  const url = 'http://localhost:8080/api/checkin';
  const payload = JSON.stringify({ ticketId: 'TICKET-999' });
  const params = { headers: { 'Content-Type': 'application/json' } };

  http.post(url, payload, params);
}