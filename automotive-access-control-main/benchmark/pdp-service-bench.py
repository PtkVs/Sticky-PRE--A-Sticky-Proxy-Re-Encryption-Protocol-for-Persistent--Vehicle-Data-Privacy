import requests
import time
import csv
import json

xaxml_reqs = [
    '{"Request":{"Category":[{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:resource","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:resource:resource-id","Value":"/vehicle/vehicleSpeed","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:subject:subject-id","Value":"smartsurance","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:action","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:action:action-id","Value":"GET","DataType":"http://www.w3.org/2001/XMLSchema#string"}]}]}}'
    #'{"Request":{"Category":[{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:resource","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:resource:resource-id","Value":"/vehicle/engineSpeed","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:subject:subject-id","Value":"smartsurance","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:action","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:action:action-id","Value":"GET","DataType":"http://www.w3.org/2001/XMLSchema#string"}]}]}}',
    #'{"Request":{"Category":[{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:resource","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:resource:resource-id","Value":"/vehicle/throttlePosition","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:subject:subject-id","Value":"smartsurance","DataType":"http://www.w3.org/2001/XMLSchema#string"}]},{"CategoryId":"urn:oasis:names:tc:xacml:3.0:attribute-category:action","Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:action:action-id","Value":"GET","DataType":"http://www.w3.org/2001/XMLSchema#string"}]}]}}'
]

csv_header = ["id", "time"]

endpoint = "http://localhost:8888/authorize"
sample_count = 100

for idx, request in enumerate(xaxml_reqs):
    req = json.loads(request)

    with open(f"{idx}.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f, delimiter=";")
        writer.writerow(csv_header)

        for i in range(sample_count):
            start = time.perf_counter()
            response = requests.post(endpoint, json=req, timeout=60)
            request_time = (time.perf_counter() - start) * 1000  # s to ms
            writer.writerow([i, request_time])
