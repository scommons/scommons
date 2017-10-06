//const path = require('path')
const ExtractTextPlugin = require('extract-text-webpack-plugin')

module.exports = {
//  target: 'web',

  module: {
    loaders: [{
      loader: "file-loader?name=[hash].[ext]&outputPath=styles/&publicPath=../",
      test: /\.(ico|png|gif|jpe?g|svg)$/i,
      exclude: /node_modules/
    }, {
      loader: ExtractTextPlugin.extract('style-loader', 'css-loader?modules&localIdentName=[local]__[hash:base64:5]&sourceMap!resolve-url-loader?sourceMap'),
      test: /\.css$/,
      exclude: /node_modules/
    }]
  },

  resolve: {
    modulesDirectories: [
      './node_modules',
      './../../../../../../scommons-client/src/main/resources'
    ]
  },

  plugins: [
    new ExtractTextPlugin('styles/styles.css')
  ]
}
