# SIARD Web Backend PoC

## Testing on Windows via VirtualBox (Linux host)

### Setup

1. Install VirtualBox 7.2+ (needed for recent kernels and Windows 11 TPM/EFI support):
   ```bash
   sudo apt install virtualbox-7.2
   ```
2. If `vboxdrv` fails to load, rebuild the kernel modules:
   ```bash
   sudo apt install build-essential linux-headers-$(uname -r)
   sudo /sbin/vboxconfig
   ```
3. Download a Windows 11 ISO from [microsoft.com](https://www.microsoft.com/software-download/windows11).
4. Create the VM: type *Windows 11 (64-bit)*, EFI enabled, 8 GB RAM, 4 CPUs, 80 GB dynamically allocated disk, ISO attached.
5. During Windows setup, skip the Microsoft account: press `Shift+F10` and run `start ms-cxh:localonly` (the old `OOBE\BYPASSNRO` was removed in newer builds).

### Transferring files into the VM

Serve the files from the host over VirtualBox's NAT network — no Guest Additions needed:

```bash
# on the host, in the folder containing the files
python3 -m http.server 8000
```

In the Windows VM, open a browser and download from `http://10.0.2.2:8000` (that IP is the host as seen from the guest). Stop the server with `Ctrl+C` when done.
