import numpy as np
import matplotlib.pyplot as plt

import pandas as pd

csv_files = [
    "0.csv"
]

labels = ["/vehicle/vehicleSpeed"]

column_names = ["id", "time"]
data = []

for file in csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()

    print(file, (sum(times) / len(times)))
    #print(file, (sum(times) / len(times)) / 1000000)

    #times = [x for x in map(lambda x: x / 1000000, times)]  # convert ns to ms

    data.append(times)
plt.boxplot(data)
plt.xticks([1], labels)
plt.ylabel("time (ms)")

plt.show()
