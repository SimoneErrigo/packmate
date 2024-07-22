<div align="center">

# Packmate
</div>

### [EN]
Advanced network traffic flow analyzer for A/D CTFs.

#### Features:
* Can monitor live traffic or analyze pcap files
* Supports binary and textual services
* Can highlight found patterns in packets
  * Substring
  * Regular expression
  * Binary substring
* Can automatically delete streams with content that you don't need
* Can make certain streams favorite and show only favorite streams
* Supports several simultaneous services, can show streams for a specific service or pattern
* Allows navigating streams using shortcuts
* Has the option to copy packet content in various formats
* Can concatenate adjacent packets
* Can urldecode text automatically
* Can automatically decompress GZIPed HTTP
* Can automatically deflate WebSockets with permessages-deflate extension
* Can automatically decrypt TLS with RSA using given private key (like Wireshark)

![Main window](screenshots/Screenshot.png)

## Full Build
Below are the instructions for those who want to build Packmate on their own.

### Setup
[Instructions](docs/SETUP_EN.md)

### Launch
After filling in env file you can launch the app:
```bash
sudo docker-compose up --build -d
```

If everything went fine, Packmate will be available on port `65000` from any host.
Database with listen on port 65001, but will only accept connections from localhost.

## Usage
[Instructions](docs/USAGE_EN.md)
