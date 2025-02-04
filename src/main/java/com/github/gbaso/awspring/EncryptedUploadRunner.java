package com.github.gbaso.awspring;

import static org.springframework.util.CollectionUtils.isEmpty;

import io.awspring.cloud.s3.S3Operations;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
class EncryptedUploadRunner implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(EncryptedUploadRunner.class);

  private final S3Client s3Client;
  private final S3Operations s3Operations;

  EncryptedUploadRunner(S3Client s3Client, S3Operations s3Operations) {
    this.s3Operations = s3Operations;
    this.s3Client = s3Client;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    List<String> methodValues = args.getOptionValues("method");
    List<String> nonOptionArgs = args.getNonOptionArgs();
    if (isEmpty(methodValues) || methodValues.size() != 1 || nonOptionArgs.size() != 2) {
      log.info("Required parameters: --method=<client|template> <bucket> <filename>");
      return;
    }
    Method method = Method.valueOf(methodValues.getFirst().toUpperCase());
    String bucket = nonOptionArgs.get(0);
    String filename = nonOptionArgs.get(1);
    log.info("Uploading file {} to bucket {} using {}", filename, bucket, method);
    Path inputFile = Path.of(filename);
    String key = inputFile.getFileName().toString();
    switch (method) {
      case CLIENT -> s3Client.putObject(put -> put.bucket(bucket).key(key), inputFile);
      case TEMPLATE -> {
        try (InputStream inputStream = Files.newInputStream(inputFile)) {
          s3Operations.upload(bucket, key, inputStream);
        }
      }
    }
    log.info("Successfully uploaded file to {}", bucket);
  }

  enum Method {
    CLIENT, TEMPLATE;
  }
}
