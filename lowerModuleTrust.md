# If you're having issues with module's and certs

dont forget you can goto: 
`"C:\Niagara\Niagara-4....\defaults\system.properties"`

and set: 

```
# Overrides the default module verification mode if necessary to adjust module signature verification requirements. Valid values are
#   low: Warnings only. Option will be removed in a future release.
#   medium (current default): Requires modules to be signed by a valid, trusted certificate.
#   high: Requires modules to be signed by a valid, trusted, CA issued certificate. Internal CAs are acceptable.

niagara.moduleVerificationMode=low`

```