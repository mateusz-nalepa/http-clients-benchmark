#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <unistd.h>
#include <time.h>
#include <errno.h>

#define PORT 12345
#define BUFFER_SIZE 4096

int main() {
            printf("Hello World\n");

    int sockfd;
    struct sockaddr_in servaddr, cliaddr;
    char buffer[BUFFER_SIZE];
    ssize_t bytes_received;
    socklen_t len = sizeof(cliaddr);
    struct timespec start, end;
    double time_spent;

    // Tworzenie gniazda
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Ustawienie gniazda na tryb nieblokujący
    int flags = fcntl(sockfd, F_GETFL, 0);
    if (fcntl(sockfd, F_SETFL, flags | O_NONBLOCK) < 0) {
        perror("fcntl failed");
        close(sockfd);
        exit(EXIT_FAILURE);
    }

    memset(&servaddr, 0, sizeof(servaddr));
    memset(&cliaddr, 0, sizeof(cliaddr));

    // Ustawienia serwera
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(PORT);
    servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    // Bindowanie gniazda
    if (bind(sockfd, (const struct sockaddr *)&servaddr, sizeof(servaddr)) < 0) {
        perror("bind failed");
        close(sockfd);
        exit(EXIT_FAILURE);
    }

    while (1) {

        // Mierzenie czasu odbierania danych
        clock_gettime(CLOCK_MONOTONIC, &start);
        bytes_received = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0, (struct sockaddr *)&cliaddr, &len);
        clock_gettime(CLOCK_MONOTONIC, &end);

        if (bytes_received < 0) {
            if (errno == EAGAIN || errno == EWOULDBLOCK) {
                // Brak danych do odebrania, kontynuowanie pętli
                usleep(100000); // Przerwa 100ms przed kolejną próbą
                                        printf("EAGAIN\n");

                continue;
            } else {
                perror("recvfrom failed");
                close(sockfd);
                exit(EXIT_FAILURE);
            }
        }

        // Obliczanie czasu
        time_spent = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
        buffer[bytes_received] = '\0'; // Upewnienie się, że bufor jest zakończony zerem
        printf("Odebrano %ld bajtów w czasie %.9f sekund\n", bytes_received, time_spent);
        printf("Dane: %s\n", buffer);

        // Sprawdzanie znaku końca
        if (strstr(buffer, "EOF") != NULL) {
            printf("Odebrano wszystkie dane.\n");
            break;
        }

        // Resetowanie bufora
        memset(buffer, 0, BUFFER_SIZE);
    }

    // Zamykanie gniazda
    close(sockfd);
    return 0;
}
