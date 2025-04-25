import matplotlib.pyplot as plt

import pandas as pd

csv_files = [
    "cpu_with_SP.csv", "cpu_with_SP_PRE.csv"
]

labels = ["SP", "SP-PRE"]

column_names = ["time", "value"]
data = []

for file in csv_files:
    df = pd.read_csv(file, names=column_names, delimiter=",", skiprows=[0])
    values = df.value.to_list()

    print(file, (sum(values) / len(values)))

    data.append(values)
plt.boxplot(data)
plt.xticks([1,2], labels)
plt.ylabel("CPU load (%)")
plt.ylim(0, 100)

plt.show()