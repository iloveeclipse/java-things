
```
alias glog="git log --oneline --name-status --all --decorate `git rev-parse --abbrev-ref HEAD 2>/dev/null`"
```

  * Shell function to show diff of given file with n-th previous version of that file:
```
# gdiff [count] <path>
# shows the diff to head on given file to the n-th commit (starting with 0 which is latest) 
# where given file was touched
# if "count" is omitted, shows the diff of the head to predecessor commit
function gdiff(){
	if [[ $# == 2 ]]
	then
		git difftool $(prev "$1" "$2") -- $2
	else
		git difftool $(prev 1 "$1") -- $1
	fi
}
```

  * Shell function to show n-th commit on given file:
```
# prev <count> <path>
# shows the n-th commit (starting with 0 which is latest) where given file was touched
function prev(){
	git log --pretty=format:"%h" -1 --skip="$1" -- $2
}
```