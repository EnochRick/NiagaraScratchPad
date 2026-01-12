# Niagara Module Maintenance common tasks

## Niagara Module transfer
Copy only modules that don’t exist Niagara old to Niagara new

`robocopy /xc /xn /xo source destination`

Where:
	Robocopy: Robocopy | Microsoft Learn
	
	/xc	Excludes existing files with the same timestamp, but different file sizes.
	/xn	Source directory files newer than the destination are excluded from the copy.
	/xo	Source directory files older than the destination are excluded from the copy.

Example
 `robocopy /xc /xn /xo C:\Niagara\Niagara-4.14.0.162\modules\ C:\Niagara\Niagara-4.15.1.16\modules\`


## Niagara Module Comparison between two directories:
	1) Open WSL (I used Ubuntu in WSL)
	2) Use the following command:
	
`diff -r /mnt/c/Niagara/Niagara-4.15.2.38/modules/ /mnt/c/temp/FX-Workbench\ 14.14.2/modules/ | grep "Only in /"`
		
		a. Where: diff -r is the diff command for comparing two directories 
		b. Where: /mnt/c/Niagara/Niagara-4.15.2.38/modules/ is your base Niagara directory
		c. Where: /mnt/c/temp/FX-Workbench\ 14.14.2/modules/  is a separate directory in C:\Temp with Fx  workbench modules (there is an escape / in this path put there by tab-to-autocomplete in WSL CLI)
		d. Where |grep"Only In /" will filter out any statements from the diff command that aren't related to modules existing in one directory and not the other
