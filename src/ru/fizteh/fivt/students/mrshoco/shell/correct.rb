file = "RmCommand.java"
text = ""
File.open(file, "r").each_line do |s|
    text += s;
end

text.gsub!(/(\s*\n\s*\{)/, ' {')
text.gsub!(/for\(/, 'for (')
text.gsub!(/if\(/, 'if (')
text.gsub!(/catch\(/, 'catch (')
text.gsub!(/^\s{4}([a-zA-Z])/, "    /**\n    *.\n    */ \n" + '    \1')
text.gsub!(/^public/, "/**\n*.\n*/ \n" + 'public')

File.open(file, "w") {|op| op.write(text)}
