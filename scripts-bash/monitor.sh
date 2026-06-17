#!/bin/bash

watch -n 2 '
printf "%-25s %8s %8s %10s %8s\n" "SERVICE" "PID" "CPU%" "RAM(MB)" "THREADS"
printf "%-25s %8s %8s %10s %8s\n" "-------------------------" "--------" "--------" "----------" "--------"

ps -eo pid,%cpu,rss,nlwp,args --sort=-rss |
grep "Notification-POC" |
grep -v grep |
while read pid cpu rss nlwp args
do
    service=$(echo "$args" | sed -n "s|.*Notification-POC/\([^/]*\)/.*|\1|p")
    ram=$((rss/1024))

    printf "%-25s %8s %8s %10s %8s\n" \
        "$service" "$pid" "$cpu" "$ram" "$nlwp"
done
'