import matplotlib.pyplot as plt
import json

from datetime import datetime

times = []
values = []

for i in range(2760):
    print(f"Reading JSON {i}")
    dict = json.load(open(f'01_22_2022-16_22_25/data_{i}.json', 'r'))
    values.append(dict['vehicle_speed'])
    times.append(datetime.strptime(dict['time'], '%Y-%m-%dT%H:%M:%S.%fZ'))

#plt.grid(True)

plt.plot_date(times, values, linestyle='solid', marker='.',  markersize=2)
plt.xlabel('Time')
plt.ylabel('Vehicle Speed (km/h)')

plt.show()