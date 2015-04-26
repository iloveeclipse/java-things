# How to use IOTester #

  * Run it like this:
```
java -jar iotester.jar <directory path> [progress per ? MB][stop after ? MB][buffer size ? KB]
```
  * (default is reporting each 10 MB, buffer size 4 KB and reading first 1 GB of data)
  * to stop just kill it via Ctrl+C or task manager of your choice

# What it does #

  * It reads each and every **accessible** file it founds under the given directory and all of it's children.
  * It skips all links to other directories
  * for big files it reads first 10 MB only
  * it reports continuously time/read spead/amount of files and dirs

# Example output #

```
andrei@pinguin ~ $ java -jar iotester.jar /var 100 1000 1024

Total files / dirs    :        630 /     162 
Total bytes read      :            105229676 ( 100.4 MB)
Elapsed time (msec)   :                 3017 (      3 sec)
Read bytes/sec        :             34878911 ( 33.3 MB/s)
Avg. bytes per file   :               167031 

Total files / dirs    :        680 /     162 
Total bytes read      :            209776456 ( 200.1 MB)
Elapsed time (msec)   :                 4203 (      4 sec)
Read bytes/sec        :             49911124 ( 47.6 MB/s)
Avg. bytes per file   :               308494 
^C
Total files / dirs    :        695 /     162 
Total bytes read      :            299554152 ( 285.7 MB)
Elapsed time (msec)   :                 5082 (      5 sec)
Read bytes/sec        :             58944146 ( 56.2 MB/s)
Avg. bytes per file   :               431013 

```

# Why??? #

  * it is portable
  * it's small
  * it can help your to explain your admin that dynamic Clearcase views are just crap.