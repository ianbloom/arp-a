# arp-a
## Introduction
This Groovy script is intended to be run as a LogicMonitor PropertySource, and uses `arp -a` to collect MAC addresses of devices on the LAN.

## Motivation
Many LogicMonitor customers run NetScans to discover devices on the LAN to add to monitoring.  With the proper credentials, we can obtain a litany of information from the device, but what information can we obtain from devices we cannot authenticate with?  It is not unreasonable to expect that a Mac workstation is not running an SNMP service, and as a result will look very similar to a networking device being queried without the correct community string.

## Execution
Enter the [Address Resolution Protocol (ARP)](https://www.cellstream.com/reference-reading/tipsandtricks/219-what-is-the-arp-command-and-how-can-i-use-it).  The "arp" command can be used to discover MAC addresses for devices on the LAN regardless of our ability to authenticate with them.  Luckily, both Windows and Linux have implementations of "arp", meaning this query can be performed regardless of the Collector type performing the query.  The data is delivered differently depending on implementation and is processed differently in the script.

The script runs `arp -a` from the perspective of the LogicMonitor Collector and builds a lookup table of IP addresses and MAC addresses on the LAN.  It collect all the IP addresses associated with a specific host in LogicMonitor.  The script then checks to see if any of the devices discovered IP addresses are in the arp lookup table.  If a match is found, the "auto.mac" property is applied to the device, and its value is the MAC address associated with the devices IP in the lookup table.

## Usage
Simply import the MAC_via_ARP PropertySource to your LogicMonitor account and it will run upon saving.

## Debugging
There is debugging information in the final block of the code that has been commented out in order to match LogicMonitor's expected output format.  Should you run into issues with this script, uncomment the appropriate print statements.