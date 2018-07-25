#! /usr/bin/env ruby
#
# generate migration file.
#
# ./migrate-gen.rb file_name

require 'pathname'

DIR_NAME = 'resources/migrations'

name = ARGV.first

unless name
  puts '[ERROR] Please enter an argument'
  exit 1
end

now = Time.now.strftime('%Y%m%d%H%M%S')
filepath =  Pathname.new(DIR_NAME) + "#{now}_#{name}.edn"

filepath.write(DATA.read)

__END__
{:up [""]
 :down [""]}
