#!/bin/perl

my %correct = (
    #"uuid"          => "",

    "beername"      => "Harpoon IPA",
    "brewery"       => "Harpoon Brewery",
    "location"      => "Boston, MA",
    "abv"           => "5.9",
    "style"         => "India Pale Ale",
    #"beernotes"     => "",

    #"user"          => "",
    #"container"     => "",
    #"stamp"         => "",
    #"drinknotes"    => "",
    #"rating"        => "",
);

my $number_to_generate = 5000;

# ==============================================================================

sub one_time_in($) {
    return ( rand() < (1/shift) );
}

sub show {
    my %beer = @_;

    print "INSERT INTO json_drink (";
    print join(", ", map {"'$_'"} keys %beer);
    print ", 'created', 'updated', 'stamp') VALUES (";
    print join(", ", map {"'" . $beer{$_} . "'"} keys %beer);
    print ", CURRENT_TIMESTAMP" x 3;
    print ");\n";
}

# ==============================================================================

for my $n (1 .. $number_to_generate) {
    my %this = %correct;

    # Wrong style sometimes
    $this{style} = "American IPA" if (one_time_in(20));
    $this{style} = "IPA" if (one_time_in(20));
    delete $this{style} if (one_time_in(5));

    # Typos on some columns
    for my $col ( qw(beername brewery location style) ) {
        if (one_time_in(40)) {
            my $char = int(rand() * length($this{$col}));
            my $val = substr($this{$col}, 0, $char);

            # Change or delete a character
            if (one_time_in(2)) {
                $val .= ('a' .. 'z')[rand() * 26];
            }

            $val .= substr($this{$col}, $char+1);
            $this{$col} = $val
        }

        delete $this{$col} if ($col ne "beername" and one_time_in(3));
    }

    # Wrong numbers on some columns
    for my $col ( qw(abv) ) {
        if (one_time_in(6)) {
            $this{$col} = int(rand() * 100) / 10;
        }

        delete $this{$col} if (one_time_in(2));
    }

    # User-specific information
    $this{user} = sprintf("617-555-%04d", int(rand() * 9999));

    $this{container} = 'Draught';
    $this{container} = 'Bottle' if (one_time_in(2));
    $this{container} = 'Can' if (one_time_in(3));

    $this{rating} = int(rand() * 5) + 1 if (one_time_in(2));

    # Generate uuid
    chomp($this{uuid} = `uuidgen`);

    show(%this);
}

