import numpy as np
import matplotlib.pyplot as plt

import pandas as pd

labels = ["Denied based\non time", "Denied based\non location", "Denied based\non polling\nfrequency", "Permitted"]

column_names = ["sample", "time", "value"]
data = []

# with ac
df = pd.read_csv("with_ac/cpu.csv", names=column_names, delimiter=";", skiprows=[0])
ac_values = df.value.to_list()

# without ac
df = pd.read_csv("without_ac/cpu.csv", names=column_names, delimiter=";", skiprows=[0])
no_ac_values = df.value.to_list()
plt.ylim(0, 100)
plt.plot(range(len(ac_values)), ac_values, label="CPU Usage (%) - With AC")
plt.plot(range(len(no_ac_values)), no_ac_values, label="CPU Usage (%) - Without AC")

#plt.tick_params(
#    axis='x',
#    which='both',
#    bottom=False,
#    top=False,
#    labelbottom=True
#)

#plt.boxplot(data)
#plt.xticks([1,2,3,4], labels)
#plt.ylabel("time (ms)")
plt.xlabel("Time (s)")
plt.legend()
plt.show()
