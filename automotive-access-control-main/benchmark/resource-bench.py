import psutil
import csv
import signal
import time

csv_fields = ["sample", "time", "value"]


def signal_handler(sig, frame):
    global terminate
    print('Finishing Benchmark!')
    terminate = True


def get_cpu_usage():
    return psutil.cpu_percent(interval=1.0)


def get_ram_usage_percent():
    return psutil.virtual_memory().percent


def get_swap_usage_percent():
    return psutil.swap_memory().percent


terminate = False

signal.signal(signal.SIGINT, signal_handler)


def main():
    global terminate
    sample_count = 0
    with open("cpu.csv", 'w', newline='', encoding='utf-8') as cpu, \
            open("memory.csv", 'w', newline='', encoding='utf-8') as memory, \
            open("swap.csv", 'w', newline='', encoding='utf-8') as swap:
        cpu_writer = csv.writer(cpu, delimiter=";")
        memory_writer = csv.writer(memory, delimiter=";")
        swap_writer = csv.writer(swap, delimiter=";")

        cpu_writer.writerow(csv_fields)
        memory_writer.writerow(csv_fields)
        swap_writer.writerow(csv_fields)

        while not terminate:
            current_time_ms = round(time.time() * 1000)

            cpu_writer.writerow([sample_count, current_time_ms, get_cpu_usage()])
            memory_writer.writerow([sample_count, current_time_ms, get_ram_usage_percent()])
            swap_writer.writerow([sample_count, current_time_ms, get_swap_usage_percent()])

            sample_count = sample_count + 1


if __name__ == "__main__":
    main()
