# pxescale  
a stupid simple /scale command

## features
### for players
- change your in-game scale using `/scale <number>`
- reset your scale quickly using `/scale reset` or `/scale`

### for admins
- change the scale of other players/entities using `/scale <number> <selector>` and `/scale reset <selector>`
- give players specific limits through permissions (`scale.min.<number>` and `scale.max.<number>`) and through plugin configuration
  - or allow players to bypass such limits with the `scale.bypass` permission

<details>
  <summary>click to view commands/permissions</summary>
  <h3><code>/scale &lt;number&gt;</code></h3>
  set your own scale
  <ul>
      <li><strong>permission:</strong> <code>scale.use</code></li>
  </ul>
  <ul>
    <li><h4><code>... &lt;selector&gt;</code></h4>
    set the scale of other players/entities
    <ul>
      <li><strong>permission:</strong> <code>scale.others</code></li>
    </ul>
    </li>
  </ul>

  <h3><code>/scale reset</code></h3>
  reset your scale back to `1.0`
  <ul>
      <li><strong>permission:</strong> <code>scale.use</code></li>
      <li><strong>aliases:</strong> <code>/scale</code></li>
  </ul>
  <ul>
    <li><h4><code>... &lt;selector&gt;</code></h4>
    reset the scale of other players/entities
    <ul>
      <li><strong>permission:</strong> <code>scale.others</code></li>
    </ul>
    </li>
  </ul>

  <h3><code>/scale reload</code></h3>
  reloads the <code>config.yml</code> file
  <ul>
      <li><strong>permission:</strong> <code>scale.reload</code></li>
  </ul>
  
  <h3>miscellaneous permissions</h3>
  <h4><code>scale.bypass</code></h4>
  allows players to bypass the scale minimums and maximums in place
  <h4><code>scale.min.&lt;number&gt;</code></h4>
  changes the minimum scale the player can use with <code>/scale</code>; this overrides the configuration's default value, even if the permission's value is larger than the configuration
  <h4><code>scale.max.&lt;number&gt;</code></h4>
  changes the maximum scale the player can use with <code>/scale</code>; this overrides the configuration's default value, even if the permission's value is larger than the configuration
</details>
