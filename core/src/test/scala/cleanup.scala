package reboot.spec

trait DispatchCleanup extends unfiltered.spec.ServerCleanup {
  override def cleanup() {
    super.cleanup()
    reboot.Http.shutdown()
  }
}
