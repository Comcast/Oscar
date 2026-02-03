.PHONY: install-jdk21 install-jdk21-linux install-jdk21-windows

install-jdk21:
	@uname_s=$$(uname -s 2>/dev/null || echo ""); \
	if [ "$$uname_s" = "Linux" ] || [ "$$uname_s" = "Darwin" ]; then \
		./scripts/install-jdk21-linux.sh; \
	else \
		powershell -NoProfile -ExecutionPolicy Bypass -File .\\scripts\\install-jdk21-windows.ps1; \
	fi

install-jdk21-linux:
	./scripts/install-jdk21-linux.sh

install-jdk21-windows:
	powershell -NoProfile -ExecutionPolicy Bypass -File .\\scripts\\install-jdk21-windows.ps1
