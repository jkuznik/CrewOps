# --- Konfiguracja ---
IMAGE_NAME := crewops-static
BUILDER := paketobuildpacks/builder:base
LOCAL_PORT := 80
CONTAINER_NAME := crewops-homepage

# Domyślny cel: wyświetlenie dostępnych celów
.PHONY: all
all: help

# Używamy .PHONY, aby upewnić się, że Make uruchomi te cele, nawet jeśli istnieją pliki o tych nazwach
.PHONY: build dockerfile-build test test-local run clean help

# --------------------

## build: Buduje obraz Docker z Buildpackami
build:
	@echo "--- 🛠️  Budowanie obrazu $(IMAGE_NAME) z Buildpackami ---"
	@if ! command -v pack &> /dev/null; then \
		echo "BŁĄD: Narzędzie 'pack' nie zostało znalezione. Zainstaluj je, aby kontynuować."; \
		exit 1; \
	fi
	pack build $(IMAGE_NAME) --builder $(BUILDER)

## dockerfile-build: (TYMCZASOWY) Buduje obraz Dockera z Dockerfile
dockerfile-build:
	@echo "--- 🛠️  (TYMCZASOWY) Budowanie obrazu $(IMAGE_NAME) z Dockerfile ---"
	@if ! command -v docker &> /dev/null; then \
		echo "BŁĄD: Docker nie został znaleziony. Upewnij się, że Docker Desktop działa."; \
		exit 1; \
	fi
	docker build -t $(IMAGE_NAME) .

## run: Uruchamia kontener Dockera na porcie 80 (bez odłączania)
run: build
	@echo "--- 🚀 Uruchamianie kontenera na http://localhost:$(LOCAL_PORT)/ ---"
	@if ! docker info &> /dev/null; then \
		echo "BŁĄD: Docker nie jest uruchomiony. Upewnij się, że Docker Desktop działa."; \
		exit 1; \
	fi
	# Zatrzymywanie poprzedniego kontenera, jeśli istnieje
	@-docker stop $(CONTAINER_NAME) 2>/dev/null || true
	@-docker rm $(CONTAINER_NAME) 2>/dev/null || true

	# Uruchomienie kontenera w trybie interaktywnym, mapowanie portu
	docker run --rm --name $(CONTAINER_NAME) -p $(LOCAL_PORT):80 $(IMAGE_NAME)

## test: Buduje Buildpackami, uruchamia w tle, prosi o testowanie i czyści.
test: build
	@echo "--- 🧪 Rozpoczynanie procesu testowania (Buildpacks) ---"
	@if ! docker info &> /dev/null; then \
		echo "BŁĄD: Docker nie jest uruchomiony. Upewnij się, że Docker Desktop działa."; \
		exit 1; \
	fi

	# Zatrzymywanie poprzedniego kontenera, jeśli istnieje
	@-docker stop $(CONTAINER_NAME) 2>/dev/null || true
	@-docker rm $(CONTAINER_NAME) 2>/dev/null || true

	# Uruchomienie w tle (detached)
	docker run -d --name $(CONTAINER_NAME) -p $(LOCAL_PORT):80 $(IMAGE_NAME)

	@echo "Kontener $(CONTAINER_NAME) uruchomiony."
	@echo "Teraz przetestuj czyste adresy URL (np. /pl/project) pod adresem: http://localhost:$(LOCAL_PORT)/"
	@read -p "Naciśnij [Enter], aby zatrzymać kontener i zakończyć test..."

	# Czyszczenie
	make clean

## test-local: (TYMCZASOWY) Buduje z Dockerfile, uruchamia w tle i czyści.
test-local: dockerfile-build
	@echo "--- 🧪 (TYMCZASOWY) Rozpoczynanie procesu testowania (Dockerfile) ---"
	@if ! docker info &> /dev/null; then \
		echo "BŁĄD: Docker nie jest uruchomiony. Upewnij się, że Docker Desktop działa."; \
		exit 1; \
	fi

	# Zatrzymywanie poprzedniego kontenera, jeśli istnieje
	@-docker stop $(CONTAINER_NAME) 2>/dev/null || true
	@-docker rm $(CONTAINER_NAME) 2>/dev/null || true

	# Uruchomienie w tle (detached)
	docker run -d --name $(CONTAINER_NAME) -p $(LOCAL_PORT):80 $(IMAGE_NAME)

	@echo "Kontener $(CONTAINER_NAME) uruchomiony."
	@echo "Adres do testu: http://localhost:$(LOCAL_PORT)/"
	@read -p "Naciśnij [Enter], aby zatrzymać kontener i zakończyć test..."

	# Czyszczenie
	make clean

## clean: Zatrzymuje i usuwa kontener testowy
clean:
	@echo "--- 🧹 Czyszczenie: Zatrzymywanie i usuwanie kontenera $(CONTAINER_NAME) ---"
	@-docker stop $(CONTAINER_NAME) 2>/dev/null || true
	@-docker rm $(CONTAINER_NAME) 2>/dev/null || true

## help: Wyświetla listę dostępnych celów
help:
	@echo "Dostępne cele (targets):"
	@awk '/^## / {printf "\033[36m%-10s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST) | sed 's/##//'