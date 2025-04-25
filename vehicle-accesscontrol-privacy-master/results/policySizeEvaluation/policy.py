# Prepare Data
import numpy as np
import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
from matplotlib import style

policy_csv_files = [
     "1.csv", "2.csv", "3.csv", "4.csv", "5.csv", "6.csv", "7.csv", "8.csv", "9.csv", "10.csv", "11.csv", "12.csv"
    , "13.csv", "14.csv", "15.csv", "16.csv", "17.csv", "18.csv", "19.csv"
]


inputdata = [8, 11, 14, 18, 21, 24, 28, 31, 35, 38, 42, 45, 48, 52, 55, 59, 62, 66, 69]

column_names = ["id", "time"]
latency= []
throughput= []

i=0
print(inputdata)
for file in policy_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()
    throughput.append((inputdata[i]/1000)/((sum(times) / len(times)) / 1000000000))
    i=i+1
print(throughput)
i=0
print(inputdata)
for file in policy_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()
    latency.append((sum(times) / len(times)) / 1000000)
    i=i+1


plt.ylim(0, 50)
plt.plot(inputdata, latency, color="blueviolet")



plt.xlabel('Policy Size (KB)')
plt.ylabel('Latency (ms)')
plt.legend()

plt.show()