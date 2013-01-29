Beware:
OSGi modules to auto-start:

QuantComponents (runlevel: 5)

com.quantcomponents.core.osgi
com.quantcomponents.core.remote.client
com.quantcomponents.marketdata.osgi.proxy
com.quantcomponents.marketdata.osgi

Infrastructure:
org.apache.felix.fileinstall
org.eclipse.ecf.osgi.services.distribution
org.apache.hadoop.zookeeper
org.eclipse.ecf.provider.zookeeper

Eclipse (normally enabled):
org.eclipse.equinox.cm
org.eclipse.equinox.ds

Make sure to NOT start:
ch.ethz.iks.slp
org.eclipse.ecf.provider.discovery # very dangerous: initialises erratically various discovery providers, which leads to system lockup when started in Eclipse
org.eclipse.ecf.provider.dnssd
org.eclipse.ecf.provider.jmdns
org.eclipse.ecf.provider.jslp
org.eclipse.ecf.provider.msn
