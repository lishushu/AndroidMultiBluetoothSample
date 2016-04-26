# AndroidMultiBluetoothSample
AndroidMultiBluetoothSample contains a bluetooth client and a bluetooth server.

Where the bluetooth server can start a bluetooth server socket listenning for 
clients' connection requests.
The bluetooth clients can scan around bluetooth devices, then connect to the server.

When the connection between client and server set up successed, the bluetooth server
will show the all connected clients. Click them will send a simple greeting to client.

PS: According to the bluetooth specification,when two Bluetooth devices come into each other’s
communication range, one of them assumes the role of master of the communication and the other becomes the
slave. This simple “one hop” network is called a piconet,
and may include up to seven active slaves connected to
one master. As a matter of fact, there is no limit on the
maximum number of slaves connected to one master but
only seven of them can be active at time, others have to
be in so called parked state. 
The specification also allows multiple roles for the same
device, i.e. a node can be a master in one piconet and a
slave in another. This permits the connection of several
piconets as the nodes functioning in master/slave mode
act gateways between piconets.

For more information about the bluetooth ad hoc networking:
http://www.netlab.tkk.fi/opetus/s38030/k02/Papers/16-Jari.pdf
