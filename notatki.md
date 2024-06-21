batchSize: 30
klientow: 20
polaczen: 150

taki sredni czas przetwarzania będzie
(90 + 180 + 270 + 360) / 4 = 225

taki sredni czas na connectionLease bedzie
(0 + 90 + 180 + 270) / 4 = 135

# mniejsza ilosc wszystkiego
2024-06-20T23:25:52.200+02:00 ERROR 104582 --- [or-http-epoll-7] NALEPA-LOGGER                            : Thread[#41,reactor-http-epoll-7,5,main] lease endpoint: ep-0000001021
czyli tutaj watek czekal, zanim będzie w stanie zrobić release :<
2024-06-20T23:25:52.309+02:00 ERROR 104582 --- [http-apache-1-6] NALEPA-LOGGER                            : Thread[#48,http-apache-1-6,5,main] release endpoint: ep-0000001021
109

przyklad 2:
2024-06-20T23:37:54.867+02:00 ERROR 106639 --- [http-apache-1-2] NALEPA-LOGGER                            : Thread[#46,http-apache-1-2,5,main] endpoint start processing on connection: c-0000000640
2024-06-20T23:37:54.987+02:00 ERROR 106639 --- [http-apache-1-2] NALEPA-LOGGER                            : Thread[#46,http-apache-1-2,5,main] endpoint end processing on connection: c-0000000640

przyklad 3:
2024-06-20T23:37:54.869+02:00 ERROR 106639 --- [http-apache-1-4] NALEPA-LOGGER                            : Thread[#48,http-apache-1-4,5,main] endpoint start processing on connection: c-0000000642
2024-06-20T23:37:55.016+02:00 ERROR 106639 --- [http-apache-1-4] NALEPA-LOGGER                            : Thread[#48,http-apache-1-4,5,main] endpoint end processing on connection: c-0000000642


# wieksza ilosc
2024-06-20T23:28:44.538+02:00 ERROR 105407 --- [or-http-epoll-3] NALEPA-LOGGER                            : Thread[#37,reactor-http-epoll-3,5,main] lease endpoint: ep-0000002001
2024-06-20T23:28:44.634+02:00 ERROR 105407 --- [http-apache-2-6] NALEPA-LOGGER                            : Thread[#57,http-apache-2-6,5,main] release endpoint: ep-0000002004
96


# czasem potrzeba po prostu wiecej polaczen i tyle? XDDDDDD
# czy tam watkow???
# percentyle nie sa jednak może aż tak najlepsze???????