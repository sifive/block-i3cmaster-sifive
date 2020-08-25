# block-i3cmaster-sifive
This is a repository for Improved inter-integrated circuit master.

1. source /india/cshrc/master.cshrc <br/>
2. source /india/proj_sifive_ip/tools/rhel_cshrc <br/>
3. set path = ( /india/proj_sifive_ip/users/kritikb/tools/install/ruby-2.5.7/bin $path ) <br/>
4. setenv WAKE_PATH $PATH <br/>
5. wit add-pkg git@github.com:sifive/environment-bangalore-sifive.git <br/>
6. wit update <br/>
7. To run simulation : wake -v 'runSimWith (i3cmasterDUT "I3CMasterConfig0") VCS_Waves' | tee log.txt <br/>
   To view waveforms : <br/>
8. setenv DISPLAY {your vnc session} <br/>
9. dve -vpd build/api-generator-sifive/i3cmasterDUT/sim/vcs/execute/demo/sim.vpd & <br/>
