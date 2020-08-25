# block-i3cmaster-sifive
This is a repository for Improved inter-integrated circuit master.

source /india/cshrc/master.cshrc <br/>
source /india/proj_sifive_ip/tools/rhel_cshrc <br/>
set path = ( /india/proj_sifive_ip/users/kritikb/tools/install/ruby-2.5.7/bin $path ) <br/>
setenv WAKE_PATH $PATH <br/>
wit add-pkg git@github.com:sifive/environment-bangalore-sifive.git <br/>
wit update <br/>
To run simulation : wake -v 'runSimWith (i3cmasterDUT "I3CMasterConfig0") VCS_Waves' | tee log.txt <br/>
To view waveforms : <br/>
setenv DISPLAY {your vnc session} <br/>
dve -vpd build/api-generator-sifive/i3cmasterDUT/sim/vcs/execute/demo/sim.vpd & <br/>
