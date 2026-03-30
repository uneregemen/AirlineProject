#!/bin/bash
clear
echo "========================================================="
echo "   🚀 STARTING NORMAL LOAD TEST: 20 VUs for 30 seconds "
echo "========================================================="
k6 run --vus 20 --duration 30s k6_script.js

echo ""
echo "========================================================="
echo "   🔥 STARTING PEAK LOAD TEST: 50 VUs for 30 seconds "
echo "========================================================="
sleep 2
k6 run --vus 50 --duration 30s k6_script.js

echo ""
echo "========================================================="
echo "   💥 STARTING STRESS LOAD TEST: 100 VUs for 30 seconds "
echo "========================================================="
sleep 2
k6 run --vus 100 --duration 30s k6_script.js

echo ""
echo "✅ ALL TESTS COMPLETED!"
echo "Lütfen ekran görüntünüzü (screenshot) bu çıktılar üzerinden alınız."
