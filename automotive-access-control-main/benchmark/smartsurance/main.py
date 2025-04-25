import numpy as np
import matplotlib.pyplot as plt

import pandas as pd

csv_files = [
    "1_out_of_time.csv", "2_out_of_location.csv", "3_out_of_polling.csv", "4_permitted.csv"
]

labels = ["Denied based\non time", "Denied based\non location", "Denied based\non polling\nfrequency", "Permitted"]

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
plt.xticks([1,2,3,4], labels)
plt.ylabel("time (ms)")

plt.show()
