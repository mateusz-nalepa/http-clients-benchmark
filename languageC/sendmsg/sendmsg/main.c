#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 12345
#define BUFFER_SIZE 16384

int main() {
    int server_fd;
    struct sockaddr_in server_addr, client_addr;
    char buffer[BUFFER_SIZE];
    const char *response = "Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello from server!Hello ";
    socklen_t addr_len = sizeof(client_addr);

    // Tworzenie gniazda plikowego
    if ((server_fd = socket(AF_INET, SOCK_DGRAM, 0)) == 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    // Ustawienia adresu
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Przypisanie adresu do gniazda
    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("bind failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("Serwer nasłuchuje na porcie %d...\n", PORT);

    while (1) {
        // Oczekiwanie na dane
        int bytes_received = recvfrom(server_fd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&client_addr, &addr_len);
        if (bytes_received < 0) {
            perror("recvfrom failed");
            continue;
        }
        buffer[bytes_received] = '\0'; // Dodanie null-terminatora
        printf("Otrzymano od klienta: %s\n", buffer);
        printf("Ide spac na 2s...\n");

        sleep(2);
        printf("Wyspalem sie\n");

        // Wysłanie odpowiedzi
        int bytes_sent = sendto(server_fd, response, strlen(response), 0, (struct sockaddr *)&client_addr, addr_len);
        if (bytes_sent < 0) {
            perror("sendto failed");
        }
    }

    // Zamknięcie gniazda
    close(server_fd);
    return 0;
}