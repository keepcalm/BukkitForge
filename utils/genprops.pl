#!/usr/bin/perl
#
use strict;
use warnings;


#######################################################################################
# GenProps - A Perl tool for generating two java .properties files from an SRG file
# SRG files used can be found at https://github.com/agaricusb/MinecraftRemapping
# Creates 2 files, "mcp.properties" and "obf.properties", containing keys as follows:
#
# - <class>_className
# - <javaName>_javaName
# - <class>_<field>_fieldName
# - <class>_<methoddesc>_desc
# - <class>_<method>_func

if (not -e "pkgmcp2obf.srg") {
	print "GenProps v1.0 - Created by keepcalm (https://github.com/keepcalm).
	Place a relevant version of pkgmcp2obf.srg (from https://github.com/agaricusb/MinecraftRemapping) in this folder and run this script.
	It will output two files, mcp.properties and obf.properties, which will each contain the following keys, mapped to their relevant values in the srg file.:

	- <class>_className
	- <javaName>_javaName
	- <class>_<field>_fieldName
	- <class>_<methoddesc>_desc
	- <class>_<method>_func\n";
}
open(FILE, "pkgmcp2obf.srg") or die "Failed to open srg file (pkgmcp2obf.srg): $!";


# mcp -> obf
my %classes;
my %fields;
my %methods;


# names (eg blockClassName -> net.minecraft.block.Block)
my %mcpMappings;
my %obfMappings;


while (<FILE>) {
	chomp;
	if (/^CL:/) {
		s/^CL: //;
		my @parts = split/ /;
		
		$classes{$parts[0]} = $parts[1];
	}
	if (/^FD:/) {
		s/^FD: //;
		my @parts = split/ /;

		$fields{$parts[0]} = $parts[1];
	}

	if (/^MD:/) {
		s/^MD: //;
		my @parts = split/ /;
		my $part1 = $parts[0] . " " . $parts[1];
		my $part2 = $parts[2] . " " .$parts[3];		
		$methods{$part1} = $part2;
	}

}
close FILE;
for (keys %classes) {
	# process classes
	my $mcp = $_;
	my $cname = (split/\//, $mcp)[-1];
	print "Class name: $cname\n";
	$cname = lcfirst $cname;
	my $obf = $classes{$_};
	
	my $jkey = $cname . "_javaName";
	my $ckey = $cname . "_className";
	print "$jkey => $mcp OR $obf\n";
	$mcpMappings{$jkey} = $mcp;
	$obfMappings{$jkey} = $obf;
	$mcp =~ s!/!\.!g;
	$obf =~ s!/!\.!g;
	print "$ckey => $mcp OR $obf\n";


	$mcpMappings{$ckey} = $mcp;
	$obfMappings{$ckey} = $obf;
}

for (keys %fields) {
	my $mcp = $_;
	my $cname = (split/\//, $mcp)[-2];
	my $fname = (split/\//, $mcp)[-1];
	$cname = lcfirst $cname;
	my $fKeyname = $fname;
	my $obf = $fields{$_};
	my $obfField = (split/\//, $obf)[-1];
	my $key = "${cname}_${fKeyname}_fieldName";
	print "$key => $obfField OR $fname\n";
	$mcpMappings{$key} = $fname;
	$obfMappings{$key} = $obfField;
}

for (keys %methods) {
	my $mcp = $_;
	my ($name, $desc) = split/ /;
	my $cname = (split/\//, $name)[-2];
	my $mname = (split/\//, $name)[-1];
	
	$cname = lcfirst $cname;
	
	my $obf = $methods{$_};
	my ($obfname, $obfdesc) = split/ /,$obf;
	my $obfMethod = (split/\//, $obfname)[-1];

	my $descKey = "${cname}_${mname}_desc";
	my $funcKey = "${cname}_${mname}_func";

	$mcpMappings{$descKey} = $desc;
	$mcpMappings{$funcKey} = $mname;

	print "$funcKey, $descKey => $mname, $desc OR $obfMethod, $obfdesc\n";

	$obfMappings{$descKey} = $obfdesc;
	$obfMappings{$funcKey} = $obfMethod;
}

open(MCP, ">", "mcp.properties") or die "Failed to open mcp.properties for writing: $!";
for (sort keys %mcpMappings) {
	print MCP "$_=$mcpMappings{$_}\n";
}
close MCP;
open(OBF, ">", "obf.properties") or die "Failed to open obf.properties for writing: $!";
for (sort keys %obfMappings) {
	print OBF "$_=$obfMappings{$_}\n";
}
