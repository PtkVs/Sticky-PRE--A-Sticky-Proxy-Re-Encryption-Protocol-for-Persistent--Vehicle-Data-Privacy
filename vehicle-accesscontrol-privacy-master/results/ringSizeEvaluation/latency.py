# Prepare Data
import numpy as np
import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
from matplotlib import style

bgv_csv_files = [
    "BGV8192.csv", "BGV16384.csv", "BGV32768.csv", "BGV65536.csv",
]
bfv_csv_files = [
    "BFV8192.csv", "BFV16384.csv", "BFV32768.csv", "BFV65536.csv",
]

inputdata = [8192, 16384, 32768, 65536]

column_names = ["id", "time"]
data_bfv= []
data_bgv= []

for file in bfv_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()

    data_bfv.append((sum(times) / len(times)) / 1000000)

for file in bgv_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()
    data_bgv.append((sum(times) / len(times)) / 1000000)

xpos = np.arange(len([8192, 16384, 32768, 65536]))

style.use('ggplot')
plt.figure(figsize=(10,7))
barWidth = 0.2
plt.bar(xpos+0.2, data_bfv, color='turquoise', width = barWidth, label='BFV')
plt.bar(xpos+0.4, data_bgv, color='blueviolet', width = barWidth, label='BGV')

plt.xticks(xpos+0.3, ('8192', '16384', '32768', '65536'))
plt.xlabel('Ring Dimensino (N)')
plt.ylabel('Latency(ms)')
plt.legend()

plt.show()