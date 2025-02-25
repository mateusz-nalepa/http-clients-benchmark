#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <errno.h>
#include <time.h>

#define PORT 12345
#define BUFFER_SIZE 65500

void set_nonblocking(int sockfd) {
    int flags = fcntl(sockfd, F_GETFL, 0);
    fcntl(sockfd, F_SETFL, flags | O_NONBLOCK);
}

const char* formattedTime() {
    static char buffer[100];
    struct timespec ts;
    struct tm *timeinfo;

    // get actual time
    clock_gettime(CLOCK_REALTIME, &ts);

    // convert to local time
    timeinfo = localtime(&ts.tv_sec);

    // convert to human readable
    strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeinfo);

    // convert to formatted time
    snprintf(buffer + strlen(buffer), sizeof(buffer) - strlen(buffer), ".%09ld", ts.tv_nsec);

    return buffer;
}

int main() {
    int client_fd;
    struct sockaddr_in server_addr;
    char buffer[BUFFER_SIZE] = "Hello from client!";
    char recv_buffer[BUFFER_SIZE];
    struct timespec start, end;
    socklen_t addr_len = sizeof(server_addr);

    // create socket
    if ((client_fd = socket(AF_INET, SOCK_DGRAM, 0)) == 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    // set an address
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    server_addr.sin_port = htons(PORT);

    // set socket in non blocking mode
     //set_nonblocking(client_fd);

    // start measuring time

    // send message to server
    //sendto(client_fd, buffer, strlen(buffer), 0, (struct sockaddr *)&server_addr, addr_len);

    // wait for message in non-blocking mode
    while (1) {
        sendto(client_fd, buffer, strlen(buffer), 0, (struct sockaddr *)&server_addr, addr_len);

        double elapsed_time;
        int bytes_received = -1;
        while (bytes_received < 0) {
            clock_gettime(CLOCK_MONOTONIC, &start);

            bytes_received = recvfrom(client_fd, recv_buffer, BUFFER_SIZE, 0, (struct sockaddr *)&server_addr, &addr_len);

            clock_gettime(CLOCK_MONOTONIC, &end);

//            elapsed_time = (end.tv_sec - start.tv_sec) * 1000.0; // s na ms
            elapsed_time = (end.tv_nsec - start.tv_nsec);// / 1000000.0; // ns na ms
//            printf("Czas odpowiedzi: %.3f ms\n", elapsed_time);
//            printf("Czas odpowiedzi: %.3f ms\n", elapsed_time);

            if (bytes_received < 0) {
                if (errno != EWOULDBLOCK && errno != EAGAIN) {
                    perror("recvfrom failed");
                    close(client_fd);
                    exit(EXIT_FAILURE);
                }
            }
            const char *currentTime = formattedTime();
            if (bytes_received < 0) {
                printf("%s : %.3f ns No data from server. I wait 1 sec...\n", currentTime, elapsed_time);
//                printf("Go to sleep for 1s...\n");
                sleep(1); // 10 ms delay to prevent busy-waiting
            }
        }
            recv_buffer[bytes_received] = '\0';
        //    printf("Got from server: %s\n", recv_buffer);
            const char *currentTime = formattedTime();

            printf("%s : %.3f ns Got %d bytes from server\n", currentTime, elapsed_time, bytes_received);
            printf("############################\n");
    }


//    printf("Response time: %.3f ms\n", elapsed_time);

    // close socket
    close(client_fd);
    return 0;
}
