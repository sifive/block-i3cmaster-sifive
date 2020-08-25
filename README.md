# block-i3cmaster-sifive
This is a repository for Improved inter-integrated circuit master.

source /india/cshrc/master.cshrc \\
source /india/proj_sifive_ip/tools/rhel_cshrc\\
set path = ( /india/proj_sifive_ip/users/kritikb/tools/install/ruby-2.5.7/bin $path )\\
setenv WAKE_PATH $PATH\\
wit add-pkg git@github.com:sifive/environment-bangalore-sifive.git\\
wit update\\
To run simulation : wake -v 'runSimWith (i3cmasterDUT "I3CMasterConfig0") VCS_Waves' | tee log.txt\\
To view waveforms :\\
setenv DISPLAY {your vnc session}\\
dve -vpd build/api-generator-sifive/i3cmasterDUT/sim/vcs/execute/demo/sim.vpd &\\
