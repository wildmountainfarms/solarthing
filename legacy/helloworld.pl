#!/usr/bin/perl

# Strict and warnings are recommended.
use strict;
use warnings;
use Data::Dumper;


use Device::SerialPort;

my $port = Device::SerialPort->new("/dev/ttyUSB0");

$port->baudrate(19200); # Configure this to match your device
$port->databits(8);
$port->parity("none");
$port->stopbits(1);

$port->dtr_active(1); #high
$port->rts_active(0); #low

print("Starting while\n");
while(1){
	my $output = $port->lookfor();
	if ($output) {
		print("Got output: $output \n");
		my @all = split("", $output);
		
		my $outputsize = scalar @all;
		if($outputsize != 48){
			print("All is: $outputsize last char value: " . ord($all[$outputsize - 1]) . "\n");
			next;
		} else {
			print ("Good size.\n");
		}
		
		
		
		my %map = (
			"inverterAddress" => 0,
			"inverterCurrent" => 00,
			"chargerCurrent" => 00,
			"buyCurrent" => 00,
			"inputVoltage" => 000,
			"outputVoltage" => 000,
			"sellCurrent" => 00,
			"operatingMode" => 00,
			"errorMode" => 000, 	# has high, middle, and low byte
			"acMode" => 00, 		# has high and low byte
			"batteryVoltage" => 00.0,
			"misc" => 000, 			# high middle and low bytes
			"warningMode" => 000, 	#high middle and low bytes
			"chksum" => 000
		);
		
		
		my $count = 1;
		foreach my $c (@all){
			$count++;
			if($c eq ","){
				next;
			} elsif($c eq chr(10)){
				next; #start
			}elsif($c eq chr(13)){ #to reverse use ord
			
				print("yay\n");
				next;
			}
			my $number = $c + 0;
			
			if($number >= 10){
				print ("This will screw some stuff up\n");
			}
			
			my $mult = 1;
			my $key = "";
			if($count == 2){
				$key = "inverterAddress";
			} elsif($count == 4 || $count == 5){
				if($count == 4){
					$mult = 10;
				}
				$key = "inverterCurrent";
				
			} elsif($count == 7 || $count == 8){
				if($count == 7){
					$mult = 10;
				}
				$key = "chargerCurrent";
			} elsif($count == 10 || $count == 11){
				if($count == 10){
					$mult = 10;
				}
				$key = "buyCurrent";
			} elsif($count == 13 || $count == 14 || $count == 15){
				if($count == 13){
					$mult = 100;
				} elsif($count == 14){
					$mult = 10;
				}
				$key = "inputVoltage";
			} elsif($count == 17 || $count == 18 || $count == 19){
				if($count == 17){
					$mult = 100;
				} elsif($count == 18){
					$mult = 10;
				}
				$key = "outputVoltage";
			} elsif($count == 21 || $count == 22){ #sellCurrent
				if($count == 21){
					$mult = 10;
				}
				$key = "sellCurrent";
			} elsif($count == 24 || $count == 25){
				if($count == 24){
					$mult = 10;
				}
				$key = "operatingMode";
				
			} elsif($count == 27 || $count == 28 || $count == 29){
				if($count == 27){
					$mult = 100;
				} elsif($count == 28){
					$mult = 10;
				}
				$key = "errorMode";
				
			} elsif($count == 31 || $count == 32){
				if($count == 31){
					$mult = 10;
				}
				$key = "acMode";
			} elsif($count == 34 || $count == 35 || $count == 36){
				if($count == 34){
					$mult = 10;
				} elsif($count == 36){
					$mult = 0.1;
				}
				$key = "batteryVoltage";
			} elsif($count == 38 || $count == 39 || $count == 40){
				if($count == 38){
					$mult = 100;
				} elsif($count == 39){
					$mult = 10;
				}
				$key = "misc";
			} elsif($count == 42 || $count == 43 || $count == 44){
				if($count == 42){
					$mult = 100;
				} elsif($count == 43){
					$mult = 10;
				}
				$key = "warningMode";
			} elsif($count == 46 || $count == 47 || $count == 48){
				if($count == 46){
					$mult = 100;
				} elsif($count == 47){
					$mult = 10;
				}
				$key = "chksum";
			} else {
				print("Something bad happened count: $count \n");
			}
			$map{$key} = $map{$key} + ($mult * $number);
			
		}
		print("eh: " . Dumper(\%map) . "\n");
		
	}
	$port->lookclear; # needed to prevent blocking
	sleep (1);
}


# Writing to the serial port
# $port->write("This message going out over serial");
