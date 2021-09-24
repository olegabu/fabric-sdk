
private void prepareMockitoForDockerContainer(){
        Mockito.when(fileUtils.savePackageToFile(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtils.generateTmpFileName(Mockito.anyString(),Mockito.anyString())).thenReturn(Path.of("./tmp/test.tar.gz"));
        Mockito.when(fileUtils.saveStreamToFile(Mockito.any(),Mockito.any()))
        .thenAnswer(invocation->{
        invocation.callRealMethod();
        Files.copy(Path.of("./tmp/test.tar.gz"),Path.of("../fabric-starter/chaincode/test.tar.gz"),StandardCopyOption.REPLACE_EXISTING);
        return Path.of("/opt/chaincode/test.tar.gz");
        });
        }
