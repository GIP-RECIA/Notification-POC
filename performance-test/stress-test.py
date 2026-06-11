import time
import uuid

import requests
from datetime import datetime, timezone


URL="http://localhost:8179/event/emit"

service="GROUPER"

API_KEY="jD60tftTJemjfldxdf4d19CQS82XZN9I"

TARGET_ID="F2000hsx"

INTERVAL=0.6

headers = {
    "Content-Type": "application/json",
    "x-api-key": API_KEY
}

print("======================================================")
print("Démarrage du stress-test")
print("Cadence programmée : 100 notifications / minute")
print("Appuyez sur Ctrl+C pour arrêter le test à tout moment.")
print("======================================================\n")

count = 0

try:
    while True:
        start_time = time.time()
        count += 1

        payload = {
            "header": {
                "eventId": str(uuid.uuid4()),
                "priority": "NORMAL",
                "service": service,
                "channels": ["WEB"],
                "createdAt": datetime.now(timezone.utc).strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
            },
            "content": {
                "title": f"Stress Test - Notification numéro : {count}",
                "message": "Stress test de la RAM à 100 notifications par minute",
                "link": ""
            },
            "target": {
                "type": "UID",
                "ids": [TARGET_ID]
            }
        }

        try:
            r = requests.post(URL, json=payload, headers=headers, timeout=5)
            if r.status_code == 202:
                print(f"message numéro : {count} envoyé avec succés")
            else:
                print(f"ERREUR: le message n'a pas pu être envoyé.")

        except Exception as e:
            print(f"ERREUR: {e}")

        elapsed = time.time() - start_time
        sleep_time = max(0, INTERVAL - elapsed)
        time.sleep(sleep_time)


except KeyboardInterrupt:
    print("\n======================================================")
    print(f"Fin du stress-test. Notifications envoyées : {count} ")
    print("======================================================")