import sys
import random
import heapq
import time
from datetime import datetime
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor

class DataManager:
    @staticmethod
    def generate_data(n):
        return random.sample(range(n * 10), n)

    @staticmethod
    def save_to_file(data, filename):
        filepath = Path(filename)
        with filepath.open("w") as f:
            f.writelines(f"{item}\n" for item in data)
        print(f"Дані збережено у файл: {filepath}")

    @staticmethod
    def load_from_file(filename):
        filepath = Path(filename)
        with filepath.open("r") as f:
            data = [int(line.strip()) for line in f]
        print(f"Дані зчитано з файлу (розмір: {len(data)})")
        return data

class HeapKthLargest:
    def __init__(self, data, k):
        self.data = data
        self.k = k
        self.n = len(data)

    def _get_top_k(self, arr):
        """Знаходить K найбільших елементів масиву i повертає весь список (купу)"""
        if not arr: return []
        actual_k = min(self.k, len(arr))
        heap = arr[:actual_k]
        heapq.heapify(heap)
        for x in arr[actual_k:]:
            if x > heap[0]:
                heapq.heapreplace(heap, x)
        return heap

    def sequential_search(self):
        """Послідовний алгоритм"""
        final_heap = self._get_top_k(self.data)
        return final_heap[0]

    def multithreaded_search(self, num_threads=4):
        """Паралельний алгоритм"""
        chunk_size = (self.n + num_threads - 1) // num_threads
        ranges = [(i, min(i + chunk_size, self.n)) for i in range(0, self.n, chunk_size)]

        def worker(start_end):
            start, end = start_end
            return self._get_top_k(self.data[start:end])

        with ThreadPoolExecutor(max_workers=num_threads) as executor:
            partial_results = list(executor.map(worker, ranges))

        
        merged = []
        for res in partial_results:
            merged.extend(res)

        final_heap = self._get_top_k(merged)
        return final_heap[0]

def main():    
    filename = "input_data.txt"

    print("Результати виконання на невеликому масиві (10 елементів, k = 3)")
    small_data = [15, 3, 9, 8, 2, 7, 1, 6, 4, 10]
    small_k = 2
    small_finder = HeapKthLargest(small_data, small_k)
    print(f"Послідовний результат: {small_finder.sequential_search()} (очікується 9)")
    print(f"Багатопотоковий:       {small_finder.multithreaded_search(2)} (очікується 9)\n")

    # Можна ініціалізувати змінні до початку циклу
    data = []
    n = 40_000_000
    k = 50_000
    
    while True:
        print("\n" + "="*40)
        print("          ГОЛОВНЕ МЕНЮ")
        print("="*40)
        print("1. Згенерувати нові дані")
        print("2. Згенерувати та зберегти нові дані")
        print("3. Зчитати дані з файлу")
        print("4. Запустити послідовний алгоритм")
        print("5. Запустити багатопотоковий алгоритм")
        print("0. Вихід з програми")
        print("="*40)
        
        choice = input("Оберіть дію (0-5): ").strip()
        
        if choice == '1':
            print("\nГенеруємо дані...")
            data = DataManager.generate_data(n)
            print("\nДані згенеровано")
            
            
        elif choice == '2':
            print("\nГенеруємо дані...")
            data = DataManager.generate_data(n)
            print("\nДані згенеровано")
            print("\nЗбереження у файл...")
            DataManager.save_to_file(data, filename)
            print("\nДані успішно збережено у файл")

            
        elif choice == '3':
            print("\nЗчитування з файлу...")
            data = DataManager.load_from_file(filename)
            print("\nДані успішно зчитані з файлу")


        elif choice == '4':
            print("\nЗапуск послідовного методу...")
            print("Послідовний алгоритм:")
            finder = HeapKthLargest(data, k)
            start_time = time.perf_counter()
            seq_result = finder.sequential_search()
            seq_time = time.perf_counter() - start_time
            print(f"Результат: {seq_result} | Час: {seq_time:.4f} сек")
        
        elif choice == '5':
            num_threads = 4
            finder = HeapKthLargest(data, k)
            while True:
                print(f"1. Запустити паралельний алгоритм ({num_threads} потоків)")
                print("2. Ввести нову к-сть потоків")
                print("0. Вийти в головне меню")
                choice_mini = input("Оберіть дію (0-2): ").strip()
                if choice_mini == '1':
                    print("Багатопотоковий алгоритм:")
                    start_time = time.perf_counter()
                    thread_result = finder.multithreaded_search(num_threads=num_threads)
                    thread_time = time.perf_counter() - start_time
                    print(f"Результат: {thread_result} | Час: {thread_time:.4f} сек")
                
                elif choice_mini == '2':
                    while True:
                        try:
                            input_temp = int(input("Введіть нову к-сть потоків (не більше 16): "))
                        except Exception:
                            print("Некоректне введення, введіть ще раз")
                        if input_temp >= 2 and input_temp <= 16:
                            num_threads = input_temp
                            break
                        else:
                            print("Некоректне введення, введіть ще раз")
                elif choice_mini == '0':
                    break
                else:
                    break
            
        elif choice == '0':
            break  
            
        else:
            print("\nПомилка: Невідома команда. Будь ласка, введіть число від 0 до 5.")

    # Запуск меню


    # n = int(input("Введіть кількість елементів (N):")) 
    # k = int(input("Введіть K:"))

    # print("Результати виконання на великому масиві: ")
    
    
    # print(datetime.time())
    # print(f"Генерація {N} елементів")
    # data = DataManager.generate_data(N)
    # DataManager.save_to_file(data, filename)
    # data = DataManager.load_from_file(filename)
    # print(datetime.time())
    
    # finder = HeapKthLargest(data, K)

    # print("-" * 30)
    # print("Послідовний алгоритм:")
    # start_time = time.perf_counter()
    # seq_result = finder.sequential_search()
    # seq_time = time.perf_counter() - start_time
    # print(f"Результат: {seq_result} | Час: {seq_time:.4f} сек")

    # print("-" * 30)
    # print("Багатопотоковий алгоритм:")
    # start_time = time.perf_counter()
    # thread_result = finder.multithreaded_search(num_threads=4)
    # thread_time = time.perf_counter() - start_time
    # print(f"Результат: {thread_result} | Час: {thread_time:.4f} сек")

if __name__ == "__main__":
    main()