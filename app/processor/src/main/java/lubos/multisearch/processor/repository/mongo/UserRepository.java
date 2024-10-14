package lubos.multisearch.processor.repository.mongo;

import lubos.multisearch.processor.dto.UserDTO;
import lubos.multisearch.processor.model.mongo.TelegramUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;


public interface UserRepository extends MongoRepository<TelegramUser, String> {

    boolean existsByTelegramId(Long telegramId);

    boolean existsByTelegramIdAndAdminTrue(Long telegramId);

    Optional<TelegramUser> findByUsername(String username);

    @Update("{ '$set' : { 'banned' : ?1 } }")
    void findAndSetBanByTelegramId(Long telegramId, boolean ban);

    @Update("{ '$set' : { 'banned' : ?1 } }")
    void findAndSetBanByUsername(String username, boolean ban);

    @Update("{ '$set' : { 'admin' : ?1 } }")
    void findAndSetAdminByTelegramId(Long telegramId, boolean admin);

    @Update("{ '$set' : { 'lastSearch.searchStr' : ?1 , 'lastSearch.pageNum' : ?2} }")
    void findAndSetLastSearchByUsername(String username, String lastSearch, Short pageNum);

    @Update("{ '$set' : { 'languageCode' : ?1 } }")
    void findAndSetUserLanguageByTelegramId(Long telegramId, String languageCode);

    @Aggregation(pipeline = {
            "{'$match': { 'telegramId' : ?0 } }",
            "{'$project': { '_id' : 0, searchStr : '$lastSearch.searchStr', pageNum : '$lastSearch.pageNum' } }"
    })
    TelegramUser.LastSearch findLastSearchByTelegramId(Long telegramId);

    @Aggregation(pipeline = {
            "{'$match': { 'username' : ?0 } }",
            "{'$project': { '_id' : '$telegramId' } }"
    })
    Optional<Long> findTelegramIdByUsername(String username);

    @Aggregation(pipeline = {"""
              {
                $lookup:
                  {
                    from: "searchDocument",
                    localField: "telegramId",
                    foreignField: "user.telegramId",
                    as: "documents"
                  }
              }
            """, """ 
              {
                $unwind:
                  {
                    path: "$documents",
                    preserveNullAndEmptyArrays: true
                  }
              }
            """, """
              {
                $group:
                  {
                    _id: {
                      telegramId: "$telegramId",
                      username: "$username",
                      admin: "$admin",
                      banned: "$banned"
                    },
                    totalFiles: {
                      $sum: {
                        $cond: [
                          {
                            $ifNull: ["$documents", false]
                          },
                          1,
                          0
                        ]
                      }
                    },
                    totalDocumentsSize: {
                      $sum: "$documents.size"
                    },
                    maxDocumentSize: {
                      $max: "$documents.size"
                    }
                  }
              }
            """, """
              {
                $project: {
                  _id: 0,
                  telegramId: "$_id.telegramId",
                  username: "$_id.username",
                  admin: "$_id.admin",
                  banned: "$_id.banned",
                  "documentsSummary.totalFiles":
                    "$totalFiles",
                  "documentsSummary.totalDocumentsSize":
                    "$totalDocumentsSize",
                  "documentsSummary.maxDocumentSize":
                    "$maxDocumentSize"
                }
             }
            """})
    Slice<UserDTO> findUsersWithDocumentSummary(Pageable pageable);
}
