import requests
from requests.auth import HTTPBasicAuth

import json

from pathlib import Path

from datetime import datetime
import time

session_time = datetime.now()
Path(f"{session_time.strftime('%m_%d_%Y-%H_%M_%S')}").mkdir(parents=True, exist_ok=True)

url = "http://localhost:8080/info"

sample = 0

while True:
    start = datetime.now()
    res = requests.get(url, auth=HTTPBasicAuth('simon', 'demo'))
    res_json = res.json()

    with open(f"data_dump/{session_time.strftime('%m_%d_%Y-%H_%M_%S')}/data_{sample}.json", 'w') as outfile:
        json.dump(res_json, outfile)

    end = datetime.now()

    diff = end - start

    print(f"[Sample {sample}] Request took {int(diff.total_seconds() * 1000)}ms!")

    sample = sample + 1
