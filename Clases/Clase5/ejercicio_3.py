import random
import string
import time

def generar_palabra(largo):
    palabra = ""
    for i in range(largo):
        palabra += random.choice(string.ascii_uppercase)
    return palabra

def main():
    inicio = time.time()  # tiempo inicial

    n = int(input("Ingresa el número de palabras a generar: "))

    palabras = []
    for i in range(n):
        palabras.append(generar_palabra(3))
    cadenota = " ".join(palabras)

    objetivo = "IPN"
    count = 0
    from_index = 0

    print("\n--- Ocurrencias ---")
    while True:
        idx = cadenota.find(objetivo, from_index)
        if idx == -1:
            break
        count += 1
        print("Posición:", idx)
        from_index = idx + 1

    print("\n--- Resultados ---")
    print("Número de palabras generadas:", n)
    print("Longitud de la cadenota:", len(cadenota))
    print("Total de ocurrencias de", objetivo, ":", count)

    mostrar = input("\n¿Quieres ver la cadenota completa? (s/n): ").lower()
    if mostrar == "s" or mostrar == "si":
        print("\nCadenota generada:")
        print(cadenota)
    else:
        print("\nCadenota no mostrada.")

    fin = time.time()  # tiempo final
    print(f"\nTiempo de ejecución: {fin - inicio:.4f} segundos")

if __name__ == "__main__":
    main()