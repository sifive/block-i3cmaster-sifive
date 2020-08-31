# block-i3cmaster-sifive
This is a repository for Improved inter-integrated circuit master.

1. source /india/cshrc/master.cshrc <br/>
2. source /india/proj_sifive_ip/tools/rhel_cshrc <br/>
3. set path = ( /india/proj_sifive_ip/users/kritikb/tools/install/ruby-2.5.7/bin $path ) <br/>
4. setenv WAKE_PATH $PATH <br/>
5. wit init workspace_i3c_test -a git@github.com:sifive/block-i3cmaster-sifive.git<br/>
6. cd workspace_i3c_test<br/>
7. wit add-pkg git@github.com:sifive/environment-bangalore-sifive.git <br/>
8. wit update <br/>
9. wake --init .
10. To run simulation : wake -x 'runSimWith (i3cmasterDUT "I3CMasterConfig0") VCS_Waves' | tee log.txt <br/>
11. setenv DISPLAY {your vnc session} <br/>
12. To view waveforms : dve -vpd build/api-generator-sifive/i3cmasterDUT/sim/vcs/execute/demo/sim.vpd & <br/>
