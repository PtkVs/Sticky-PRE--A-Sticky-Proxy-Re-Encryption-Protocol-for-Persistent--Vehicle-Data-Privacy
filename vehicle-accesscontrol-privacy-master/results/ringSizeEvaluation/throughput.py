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
i=0
print(inputdata)
for file in bfv_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()
    data_bfv.append((inputdata[i]/1000)/((sum(times) / len(times)) / 1000000000))
    i=i+1

i=0
for file in bgv_csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=";", skiprows=[0])
    times = df.time.to_list()
    data_bgv.append((inputdata[i]/1000)/((sum(times) / len(times)) / 1000000000))
    i=i+1

plt.ylim(0, 400)
plt.plot(inputdata, data_bfv, color="turquoise", label="BFV")
plt.plot(inputdata, data_bgv, color="blueviolet", label="BGV")



plt.xlabel('Ring Dimensino (N)')
plt.ylabel('Throughput (KB/s)')
plt.legend()

plt.show()