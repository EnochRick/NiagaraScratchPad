# BACnet Introduction — Summary for IT Network Engineers
## What is BACnet?
BACnet (Building Automation Control Network) is an ANSI/ISO standard data communication protocol for building automation systems — HVAC, fire, lighting, security, elevators, etc. As an IT network engineer, you'll encounter it when building automation systems share your IP infrastructure.
---
## Network Transport Types (Most Important for IT)
BACnet supports seven MAC/transport types. The ones most likely to affect your network:
**BACnet/IP** — The most IT-relevant. It runs over UDP/IP and can share existing Ethernet, VLAN, and WAN infrastructure. Devices plug into standard Ethernet switches. Key caveat: it uses **UDP broadcasts**, which cannot cross IP subnet boundaries. When BACnet/IP spans multiple subnets, **BACnet Broadcast Management Devices (BBMDs)** must be deployed on each subnet to forward those broadcasts. This is the most important thing for an IT engineer to understand — you may need to plan for or configure BBMDs, or ensure firewall/ACL rules allow UDP BACnet traffic (default port **47808/UDP**).
**BACnet MS/TP** — Runs over **EIA-485 twisted pair**, up to 4,000 feet. This is a separate, isolated serial network that typically doesn't touch your IP infrastructure unless bridged through a gateway or router.
**BACnet ISO 8802-3 (Ethernet)** — Runs directly on Ethernet frames (not IP). Similar speed to BACnet/IP but limited to a single physical network segment with no IP routing.
**BACnet over ZigBee** — Wireless mesh, typically used as a gateway to low-cost sensors. Not a native BACnet transport.
---
## BACnet Routers
BACnet routers link dissimilar network types (e.g., BACnet/IP ↔ MS/TP). They pass BACnet messages between segments without altering message content. They are often built into automation controllers rather than being standalone devices — something to be aware of when troubleshooting or segmenting traffic.
---
## Network Traffic Considerations
**Device Discovery (Who-Is / I-Am)** — BACnet uses broadcast-based discovery. Devices broadcast "Who-Is" requests and others respond with "I-Am." This means BACnet broadcast traffic will be present on any subnet with BACnet/IP devices. Excessive broadcast traffic can be a concern on large flat networks.
**Change of Value (COV)** — A subscription-based mechanism where devices only send updates when a value changes beyond a threshold, reducing polling traffic. Clients must periodically re-subscribe to keep connections alive, which generates some background traffic.
**Trending** — Distributed data sampling, where edge devices store trend logs locally and upload in bulk to a supervisory system. This produces periodic bursts of data transfer rather than constant streams.
---
## Network Security
The document notes that BACnet includes a network security layer for applications involving access control, sensitive physical venues, or systems exposed to the public internet. This is worth flagging — BACnet systems connected to corporate IP networks without proper segmentation (VLANs, firewall rules) can be a security risk. Best practice is to isolate BACnet/IP traffic on a dedicated VLAN.
---
## Device & Network Management
BACnet supports dynamic device binding using Who-Is/I-Am broadcasts. Each device must have a **unique Device Instance number** across the entire BACnet internetwork. Duplicate instance numbers cause communication failures — relevant if you're expanding or merging BACnet networks.
---
## Enterprise Integration
BACnet has defined **Web Services (BACnet/WS)** using HTTP over TCP/IP and XML schemas, allowing enterprise applications to query building automation data. This is where BACnet crosses more directly into traditional IT territory and may require firewall rules or API gateway consideration.
---
## Key Takeaways for an IT Network Engineer
- Plan for **UDP broadcast traffic** on any subnet with BACnet/IP devices
- Deploy or verify **BBMDs** if BACnet spans multiple IP subnets
- Consider **VLAN segmentation** to isolate BACnet traffic from corporate traffic
- Be aware that **BACnet routers** are often embedded in building controllers, not separate appliances
- UDP port **47808** is the standard BACnet/IP port — ensure it's permitted where needed
- The **MS/TP** serial segments are separate from your IP network but connect via gateways/routers
