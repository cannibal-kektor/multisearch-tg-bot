package lubos.multisearch.processor.repository.mongo;

import lubos.multisearch.processor.model.mongo.SearchDocument;
import lubos.multisearch.processor.model.mongo.TelegramUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DocumentRepository extends MongoRepository<SearchDocument, String> {

    Page<SearchDocument> findByUser(TelegramUser user, Pageable pageable);

    Integer deleteByDocumentNameAndUser(String filename, TelegramUser user);

    Integer deleteAllByUser(TelegramUser user);

//
//    @Aggregation("{ $group: { _id : $telegramId, totalFiles : { $sum: 1 } , totalDocumentsSize : { $sum : $size }, maxDocumentSize : { $max : $size }  } }")
//    List<UserDTO.UserDocumentsSummaryDto> findPerUserDocumentSummary();

//    @Aggregation(pipeline = {"""
//            {
//              $group: {
//                _id: "$user.telegramId",
//                totalFiles: {
//                  $sum: 1
//                },
//                totalDocumentsSize: {
//                  $sum: "$size"
//                },
//                maxDocumentSize: {
//                  $max: "$size"
//                }
//              }
//            }""", """
//            {
//              $lookup:
//                {
//                  from: "telegramUser",
//                  localField: "_id",
//                  foreignField: "telegramId",
//                  as: "users"
//                }
//            }""", """
//            {
//              $unwind: "$users"
//            }""", """
//              {
//                $project:
//                  {
//                    telegramId: "$_id",
//                    username: "$users.username",
//                    admin: "$users.admin",
//                    banned: "$users.banned",
//                    "documentsSummary.totalFiles":
//                      "$totalFiles",
//                    "documentsSummary.totalDocumentsSize":
//                      "$totalDocumentsSize",
//                    "documentsSummary.maxDocumentSize":
//                      "$maxDocumentSize"
//                  }
//              }
//            """})
//    Slice<UserDTO> findUsersWithDocumentSummary(Pageable pageable);

}
