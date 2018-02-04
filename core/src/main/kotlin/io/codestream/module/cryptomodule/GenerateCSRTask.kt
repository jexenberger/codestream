package io.codestream.module.cryptomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.crypto.JavaKeyStore
import io.codestream.util.crypto.SSLCertificate
import java.io.File
import java.util.*
import javax.validation.constraints.NotEmpty

class GenerateCSRTask : Task, SetOutput {

    @TaskProperty(description = "Certificate CN (Command Name)")
    @get:NotEmpty
    var cn: String = ""

    @TaskProperty(description = "Certificate OU (Organization Unit)")
    @get:NotEmpty
    var ou: String = ""

    @TaskProperty(description = "Certificate Organization attribute")
    @get:NotEmpty
    var org: String = ""

    @TaskProperty(description = "Certificate Locality attribute")
    @get:NotEmpty
    var locality: String = ""

    @TaskProperty(description = "Certificate State attribute")
    @get:NotEmpty
    var state: String = ""

    @TaskProperty(description = "Certificate Country attribute")
    @get:NotEmpty
    var country: String = Locale.getDefault().isO3Country

    @TaskProperty(description = "Certificate Algorithm")
    @get:NotEmpty
    var algorithm: String = "RSA"

    @TaskProperty(description = "Certificate Signature type")
    @get:NotEmpty
    var signature: String = "SHA256WithRSA"

    @TaskProperty(description = "Certificate varidity period (days)")
    @get:NotEmpty
    var validity: Long = 730

    @TaskProperty(description = "Key size")
    @get:NotEmpty
    var keySize: Int = 2048

    @TaskProperty(description = "Output variable to store resulting CSR, format is a Map with 'public' storing the public key, 'private' storing the private Key and 'csr' storing the certificate string")
    @get:NotEmpty
    override var outputVar: String = "_keyAndCsr"

    @TaskProperty(description = "Path to JKS format key store to store resulting key")
    var jksFile: File? = null

    @TaskProperty(description = "Path to output CSR in plain text to")
    var csrOutputfile: File? = null

    @TaskProperty(description = "Path to JKS format key store to store resulting key")
    @get:NotEmpty
    var jksFilePassphrase: String = "changeit"

    @TaskProperty(description = "Path to key store to store resulting key and CSR")
    var certificateAlias: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val certAndCSR = SSLCertificate(
                cn = cn,
                ou = ou,
                org = org,
                locality = locality,
                state = state,
                country = country,
                algorithm = algorithm,
                signature = signature,
                validity = validity,
                keySize = keySize
        ).pkcs10CSR()
        ctx[outputVar] = mutableMapOf(
                "public" to certAndCSR.first.public,
                "private" to certAndCSR.first.private,
                "csr" to certAndCSR.first
        )
        csrOutputfile?.writeText(certAndCSR.second)
        return jksFile?.let {
            if (!it.exists() && it.isDirectory) {
                invalidParameter(id, "${it.absolutePath} does not exist or is a directory")
            }
            if (certificateAlias.isEmpty()) {
                invalidParameter(id, "certificate alias is a required property if you want to store to a Keystore")
            }
            JavaKeyStore(it, jksFilePassphrase).addKeyAndCertificate(certificateAlias, certAndCSR.first.private)
            done()
        } ?: done()
    }
}