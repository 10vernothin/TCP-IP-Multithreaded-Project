# TCP-IP-Multithreaded-Project
This is an extension of the simple TCP-IP project. This particular solution is server-side handling multiple threads and client-side handling multiple sockets in a single thread. All sockets are handled so the minimum amount of leakage will happen and is thread-safe.

Extract this file, run IDE, run MultiServerMain to open the server, then run FileReaderClientMain to extract the Star Spangled Banner from the extractor_file.txt into the blank receiver_file.

If you wish to change the targets of the files, change the file paths.
If you wish to change the number of threads running at a time, change the number of sockets on FileReaderClientMain.
If you wish to play with input prompt, run InputClientMain.
If you wish to change the machine target of the socket, change the MachineName of the sockets.

The project will support file reading API in the future, which just reflects back extractor_file.txt onto the console instead of writing it onto a file.
