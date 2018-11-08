// Obtain Collector platform due to different implementations of arp
collector_platform = hostProps.get("system.collectorplatform")

// Get list of host IPs
ips = hostProps.get("system.ips")
device_ip_array = ips.split(",")

// Collect mac addresses on LAN
arp_result = "arp -a".execute().text

// Initialize holder dictionary and format data
holder_dict = [:]

//////////////////////
// Windows Collector//
//////////////////////

if(collector_platform == 'windows') {
    count = 0
    // Iterate through each line of arp_result and process
    arp_result.eachLine { line ->
        // Skip header lines
        if(count > 2) {
            line_array = line.split([' '])
            // Remove null elements
            line_array = line_array.findAll { item -> !item.isEmpty() }
            // Trim parentheses
            ip_addr = line_array[0].replace("(", "").replace(")", "")
            mac_addr = line_array[1].replaceAll("-",":")
            // Append MAC if there is more than one, else initialize
            if(holder_dict.containsKey(ip_addr)) {
                holder_dict["${ip_addr}"] = holder_dict["${ip_addr}"] + ", ${mac_addr}"
            }
            else {
                holder_dict["${ip_addr}"] = mac_addr
            }
        }
        // Iterate
        count += 1
    }
}

/////////////////////
// Linux Collector //
/////////////////////

else {
    // do nothing for now
    arp_result.eachLine { line ->
        line_array = line.split([' '])
        ip_addr = line_array[1].replace("(", "").replace(")", "")
        mac_addr = line_array[3]
        if(holder_dict.containsKey(ip_addr)) {
            holder_dict["${ip_addr}"] = holder_dict["${ip_addr}"] + ", ${mac_addr}"
        }
        else {
            holder_dict["${ip_addr}"] = mac_addr
        }
    }
}

// Check if ips is a member of holder_dicts keys
// possible_mac will only be 'true' if it is present in holder_dict, if so it'll be its value (mac)
device_ip_array.each{ device_ip ->
    println("Checking if ${device_ip} was discovered during 'arp -a'")
    possible_mac = holder_dict.find{ it.key == "${device_ip}" }?.value
    if(possible_mac == null) {
        println("${device_ip} was not discovered")
    }
    else {
        println("auto.mac=${possible_mac}")
    }
}

return 0;