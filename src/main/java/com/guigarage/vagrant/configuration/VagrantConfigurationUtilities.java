package com.guigarage.vagrant.configuration;

import java.net.URL;
import java.util.UUID;

public class VagrantConfigurationUtilities {

	private VagrantConfigurationUtilities() {

	}

	public static String createVagrantFileContent(
			VagrantEnvironmentConfig config) {
		StringBuilder builder = new StringBuilder();
		builder.append("Vagrant::Config.run do |config|").append("\n");
		for (VagrantVmConfig vmConfig : config.getVmConfigs()) {
			String vmName = vmConfig.getName();
			if (vmName == null) {
				vmName = UUID.randomUUID().toString();
			}
			builder.append(
					"config.vm.define :" + vmName + " do |" + vmName
							+ "_config|").append("\n");

			for (VagrantPortForwarding portForwarding : vmConfig
					.getPortForwardings()) {
				String portForwardingName = portForwarding.getName();
				if (portForwardingName != null) {
					builder.append(
							"config.vm.forward_port \""
									+ portForwardingName + "\", "
									+ portForwarding.getGuestport() + ", "
									+ portForwarding.getHostport())
							.append("\n");
				} else {
					builder.append(
							"config.vm.forward_port "
									+ portForwarding.getGuestport() + ", "
									+ portForwarding.getHostport())
							.append("\n");
				}
			}
			builder.append(
					"db_config.vm.box = \"" + vmConfig.getBoxName() + "\"")
					.append("\n");

			URL boxUrl = vmConfig.getBoxUrl();
			if (boxUrl != null) {
				builder.append(
						"config.vm.box_url = \"" + boxUrl + "\"")
						.append("\n");
			}

			PuppetProvisionerConfig puppetProvisionerConfig = vmConfig.getPuppetProvisionerConfig();
			if(puppetProvisionerConfig != null) {
				builder.append("config.vm.provision :puppet do |puppet|").append("\n");
				builder.append("puppet.manifests_path = \"" + puppetProvisionerConfig.getManifestsPath() + "\"").append("\n");
				builder.append("puppet.manifest_file  = \"" + puppetProvisionerConfig.getManifestFile() + "\"").append("\n");
				builder.append("end").append("\n");
			}			
			builder.append("end").append("\n");
		}
		builder.append("end").append("\n");
		return builder.toString();
	}
}
